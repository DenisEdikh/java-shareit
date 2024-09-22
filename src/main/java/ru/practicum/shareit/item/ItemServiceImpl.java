package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoTime;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, NewItemDto newItemDto) {
        log.debug("Started checking contains user with userId {} in method create", userId);
        final User user = userService.checkUserById(userId);
        log.debug("Finished checking contains user with userId {} in method create", userId);
        final Item item = ItemMapper.toItem(user, newItemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, Long userId, NewCommentDto newCommentDto) {
        log.debug("Started checking contains user with userId {} in method createComment", userId);
        final User user = userService.checkUserById(userId);
        final Item item = checkItemById(itemId);
        final Booking booking = bookingRepository
                .findBookingByBookerIdAndItemIdAndStatus(userId, itemId, Status.APPROVED)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
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
        userService.checkUserById(userId);
        final Item item = checkItemById(itemId);
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
    public List<ItemDto> getAllByUserId(Long userId) {
        log.debug("Started checking contains user with userId {} in method getAllByUserId", userId);
        userService.checkUserById(userId);
        log.debug("Finished checking contains user with userId {} in method getAllByUserId", userId);
        return ItemMapper.toItemDto(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public ItemDtoTime getById(Long itemId) {
        LocalDateTime ldt = LocalDateTime.now();
        final List<Comment> comments = getCommentsByItemId(itemId);
        final Booking lastBooking = bookingRepository
                .findFirstByItemIdAndEndIsBeforeAndStartIsAfterOrderByEndDesc(itemId, ldt, ldt)
                .orElse(null);
        final Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStartAsc(itemId, ldt)
                .orElse(null);
        return ItemMapper.toItemDtoTime(checkItemById(itemId), comments, lastBooking, nextBooking);
    }

    @Override
    public List<ItemDto> getBySearch(Long userId, String text) {
        log.debug("Started checking contains user with userId {} in method getBySearch", userId);
        userService.checkUserById(userId);
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

    public Item checkItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Item with id {} not found ", itemId);
            return new NotFoundException(String.format("Item with id = %d not found ", itemId));
        });
    }

    private void checkOwnerByItem(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.warn("Only owner can change item");
            throw new ConditionsNotMetException("Only owner can change item");
        }
    }

    private void checkEndTime(Booking booking) {
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new InvalidRequestException("End time is after now time");
        }
    }
}
