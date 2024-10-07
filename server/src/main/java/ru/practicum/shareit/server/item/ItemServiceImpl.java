package ru.practicum.shareit.server.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.comment.Comment;
import ru.practicum.shareit.server.comment.CommentMapper;
import ru.practicum.shareit.server.comment.CommentRepository;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.dto.NewCommentDto;
import ru.practicum.shareit.server.exception.ConditionsNotMetException;
import ru.practicum.shareit.server.exception.InvalidRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.ItemAllDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.NewItemDto;
import ru.practicum.shareit.server.item.dto.UpdateItemDto;
import ru.practicum.shareit.server.request.ItemRequest;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, NewItemDto newItemDto) {
        log.debug("Started checking contains user with userId {} in method create", userId);
        final User user = checkUserIsContained(userId);
        log.debug("Finished checking contains user with userId {} in method create", userId);
        final ItemRequest itemRequest = checkItemRequestIsContained(newItemDto.getRequestId());
        final Item item = ItemMapper.toItem(user, newItemDto, itemRequest);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, Long userId, NewCommentDto newCommentDto) {
        log.debug("Started checking contains user with userId {} in method createComment", userId);
        final User user = checkUserIsContained(userId);
        final Item item = checkItemIsContained(itemId);
        final List<Booking> booking = bookingRepository
                .findBookingByBookerIdAndItemIdAndStatus(userId, itemId, Status.APPROVED);
        checkEndTime(booking);
        log.debug("Finished checking contains user with userId {} in method createComment", userId);
        final Comment comment = CommentMapper.toComment(item, user, newCommentDto);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, UpdateItemDto updateItemDto) {
        log.debug("Started checking contains user with userId {} and item with itemId {} in method update",
                userId,
                itemId);
        checkUserIsContained(userId);
        //todo сдулать join fetch
        final Item item = checkItemIsContained(itemId);
        log.debug("Finished checking contains user with userId {} and item with itemId {} in method update",
                userId,
                itemId);
        checkOwnerByItem(item, userId);
        if (Objects.nonNull(updateItemDto.getName()) && !updateItemDto.getName().isBlank()) {
            item.setName(updateItemDto.getName());
        }
        if (Objects.nonNull(updateItemDto.getDescription()) && !updateItemDto.getDescription().isBlank()) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (Objects.nonNull(updateItemDto.getAvailable())) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemAllDto> getAllByUserId(Long userId) {
        //todo проверить join fetch
        log.debug("Started checking contains user with userId {} in method getAllByUserId", userId);
        checkUserIsContained(userId);
        log.debug("Finished checking contains user with userId {} in method getAllByUserId", userId);
        LocalDateTime ldt = LocalDateTime.now();
        // key-itemId, value-item
        Map<Long, Item> itemMap = itemRepository.findAllByOwnerId(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        // key-itemId, value-booking
        Map<Long, Booking> bookingLastMap = bookingRepository
                .findLastBookingWith(itemMap.keySet(), userId, ldt)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        Map<Long, Booking> bookingNextMap = bookingRepository
                .findNextBookingWith(itemMap.keySet(), userId, ldt)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        Map<Long, List<Comment>> commentMap = commentRepository
                .findByItemIdIn(itemMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getItem().getId()));
        return itemMap.values()
                .stream()
                .map(item -> ItemMapper.toItemAllDto(item,
                        commentMap.get(item.getId()),
                        bookingLastMap.get(item.getId()),
                        bookingNextMap.get(item.getId()))
                )
                .toList();
    }

    @Override
    public ItemAllDto getById(Long itemId, Long userId) {
        LocalDateTime ldt = LocalDateTime.now();
        Sort lastSort = Sort.by(Sort.Direction.DESC, "end");
        Sort nextSort = Sort.by(Sort.Direction.ASC, "start");

        checkUserIsContained(userId);
        final List<Comment> comments = getCommentsByItemId(itemId);

        final Booking lastBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndEndIsBefore(itemId, userId, ldt, lastSort)
                .orElse(null);
        final Booking nextBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndStartIsAfter(itemId, userId, ldt, nextSort)
                .orElse(null);
        final Item item = checkItemIsContained(itemId);
        return ItemMapper.toItemAllDto(item, comments, lastBooking, nextBooking);
    }

    @Override
    public List<ItemDto> getBySearch(Long userId, String text) {
        log.debug("Started checking contains user with userId {} in method getBySearch", userId);
        checkUserIsContained(userId);
        log.debug("Finished checking contains user with userId {} in method getBySearch", userId);

        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            BooleanExpression byName = QItem.item.name.containsIgnoreCase(text);
            BooleanExpression byDescription = QItem.item.description.containsIgnoreCase(text);
            BooleanExpression byAvailable = QItem.item.available.isTrue();
            return ItemMapper.toItemDto(itemRepository.findAll((byName.or(byDescription)).and(byAvailable)));
        }
    }

    @Override
    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId);
    }

    private Item checkItemIsContained(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with id {} not found ", itemId);
            return new NotFoundException(String.format("Item with id = %d not found ", itemId));
        });
    }

    private User checkUserIsContained(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found", userId);
            return new NotFoundException(String.format("User with id = %d not found", userId));
        });
    }

    private ItemRequest checkItemRequestIsContained(Long requestId) {
        if (requestId != null) {
            return itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("ItemRequest not found"));
        }
        return null;
    }

    private void checkOwnerByItem(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.warn("Only owner can change item");
            throw new ConditionsNotMetException("Only owner can change item");
        }
    }

    private void checkEndTime(List<Booking> booking) {
        if (!booking.stream().anyMatch(booking1 -> booking1.getEnd().isBefore(LocalDateTime.now()))) {
            throw new InvalidRequestException("End time is after now time");
        }
    }
}
