package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.Status;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTestIT {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private NewItemDto newItemDto;
    private User owner;
    private User user;
    private ItemRequest itemRequest;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("name");
        owner.setEmail("email@email.com");
        owner = userRepository.save(owner);

        user = new User();
        user.setName("name2");
        user.setEmail("email2@email.com");
        user = userRepository.save(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(user);
        itemRequest = itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setOwner(owner);
        item.setAvailable(Boolean.TRUE);
        item = itemRepository.save(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        newItemDto = new NewItemDto("name", "description", Boolean.TRUE, itemRequest.getId());
    }

    @Test
    void createItemWhenAllValid() {
        ItemDto itemDto = itemService.create(owner.getId(), newItemDto);

        assertNotNull(itemDto);
        assertNotNull(itemDto.getId());
        assertEquals(newItemDto.getName(), itemDto.getName());
        assertEquals(newItemDto.getDescription(), itemDto.getDescription());
        assertEquals(newItemDto.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void createItemWhenUserIsMissing() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.create(Long.MAX_VALUE, newItemDto));

        assertEquals(exception.getMessage(), String.format("User with id = %d not found", Long.MAX_VALUE));
    }

    @Test
    void createCommentWhenAllValid() {
        NewCommentDto newCommentDto = new NewCommentDto("Good item");
        CommentDto commentDto = itemService.createComment(item.getId(), user.getId(), newCommentDto);

        assertNotNull(commentDto);
        assertNotNull(commentDto.getId());
        assertEquals(commentDto.getText(), newCommentDto.getText());
        assertEquals(commentDto.getAuthorName(), user.getName());
    }

    @Test
    void createCommentWhenUserIsMissing() {
        NewCommentDto newCommentDto = new NewCommentDto("Good item");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(item.getId(), Long.MAX_VALUE, newCommentDto));

        assertEquals(exception.getMessage(), String.format("User with id = %d not found", Long.MAX_VALUE));
    }

    @Test
    void createCommentWhenItemIsMissing() {
        NewCommentDto newCommentDto = new NewCommentDto("Good item");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(Long.MAX_VALUE, user.getId(), newCommentDto));

        assertEquals(exception.getMessage(), String.format("Item with id = %d not found ", Long.MAX_VALUE));
    }

    @Test
    void createCommentWhenBookingIsNotEnded() {
        NewCommentDto newCommentDto = new NewCommentDto("Good item");
        booking.setEnd(LocalDateTime.now().plusDays(2));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> itemService.createComment(item.getId(), user.getId(), newCommentDto));

        assertEquals(exception.getMessage(), "End time is after now time");
    }

    @Test
    void updateNameOfItemWhenAllValid() {
        UpdateItemDto updateItemDto = new UpdateItemDto("newName", null, null);

        ItemDto itemDto = itemService.update(item.getId(), owner.getId(), updateItemDto);

        assertNotNull(itemDto);
        assertEquals(updateItemDto.getName(), itemDto.getName());
        assertNotEquals(itemDto.getDescription(), updateItemDto.getDescription());
    }

    @Test
    void updateDescriptionOfItemWhenAllValid() {
        UpdateItemDto updateItemDto = new UpdateItemDto(null, "newDescription", null);

        ItemDto itemDto = itemService.update(item.getId(), owner.getId(), updateItemDto);

        assertNotNull(itemDto);
        assertNotEquals(itemDto.getName(), updateItemDto.getName());
        assertEquals(updateItemDto.getDescription(), itemDto.getDescription());
    }

    @Test
    void updateAvailableOfItemWhenAllValid() {
        UpdateItemDto updateItemDto = new UpdateItemDto(null, null, Boolean.FALSE);

        ItemDto itemDto = itemService.update(item.getId(), owner.getId(), updateItemDto);

        assertNotNull(itemDto);
        assertNotEquals(updateItemDto.getDescription(), itemDto.getDescription());
        assertEquals(itemDto.getAvailable(), updateItemDto.getAvailable());
    }

    @Test
    void updateItemWhenUserIsMissing() {
        UpdateItemDto updateItemDto = new UpdateItemDto(null, null, Boolean.FALSE);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(item.getId(), Long.MAX_VALUE, updateItemDto));

        assertEquals(exception.getMessage(), String.format("User with id = %d not found", Long.MAX_VALUE));
    }

    @Test
    void updateItemWhenItemIsMissing() {
        UpdateItemDto updateItemDto = new UpdateItemDto(null, null, Boolean.FALSE);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(Long.MAX_VALUE, user.getId(), updateItemDto));

        assertEquals(exception.getMessage(), String.format("Item with id = %d not found ", Long.MAX_VALUE));
    }

    @Test
    void updateItemWhenUserIsNotOwner() {
        UpdateItemDto updateItemDto = new UpdateItemDto(null, null, Boolean.FALSE);

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> itemService.update(item.getId(), user.getId(), updateItemDto));

        assertEquals(exception.getMessage(), "Only owner can change item");
    }

    @Test
    void getAllItemsByOwnerId() {
        List<ItemAllDto> items = itemService.getAllByUserId(owner.getId());

        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    void getAllWhenItemsByUserIsNotOwner() {
        List<ItemAllDto> items = itemService.getAllByUserId(user.getId());

        assertNotNull(items);
        assertEquals(0, items.size());
    }


    @Test
    void getById() {
        ItemAllDto itemAllDto = itemService.getById(item.getId(), user.getId());

        assertNotNull(itemAllDto);
        assertNotNull(itemAllDto.getId());
        assertEquals(itemAllDto.getName(), item.getName());
        assertEquals(itemAllDto.getDescription(), item.getDescription());
    }


    @Test
    void getBySearchWhenContained() {
        List<ItemDto> items = itemService.getBySearch(user.getId(), "ame");

        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    void getBySearchWhenNotContained() {
        List<ItemDto> items = itemService.getBySearch(user.getId(), "man");

        assertNotNull(items);
        assertEquals(0, items.size());
    }

//    @Test
//    void getCommentsByItemId() {
//List<>
//    }
}