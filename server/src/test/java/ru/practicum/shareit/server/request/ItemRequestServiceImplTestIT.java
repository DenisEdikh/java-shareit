package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.server.request.dto.NewItemRequestDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTestIT {
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private User user;
    private User user2;
    private NewItemRequestDto newItemRequestDto;

    @BeforeEach
    void setUp() {
        User newUser = new User();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        user = userRepository.save(newUser);

        User newUser2 = new User();
        newUser2.setName("name2");
        newUser2.setEmail("email2@email.com");
        user2 = userRepository.save(newUser2);

        newItemRequestDto = new NewItemRequestDto("description");
    }

    @Test
    void createItemRequest_WhenUserIdValid() {
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);

        assertNotNull(itemRequestDto);
        assertNotNull(itemRequestDto.getId());
        assertNotNull(itemRequestDto.getCreated());
        assertEquals(newItemRequestDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void createItemRequest_WhenUserIdNotValid() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(Long.MAX_VALUE, newItemRequestDto));

        assertEquals(notFoundException.getMessage(), String.format("User with id = %d not found", Long.MAX_VALUE));
    }

    @Test
    void getItemRequestsByValidUserId() {
        NewItemRequestDto newItemRequestDto2 = new NewItemRequestDto("description2");

        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);
        ItemRequestDto itemRequestDto2 = itemRequestService.createItemRequest(user.getId(), newItemRequestDto2);
        List<ItemRequestWithItemDto> itemRequestList = itemRequestService.getItemRequestsByUserId(user.getId());

        assertNotNull(itemRequestList);
        assertEquals(2, itemRequestList.size());
    }

    @Test
    void getItemRequestsByNotValidUserId() {
        NewItemRequestDto newItemRequestDto2 = new NewItemRequestDto("description2");

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(Long.MAX_VALUE, newItemRequestDto));

        assertEquals(notFoundException.getMessage(), String.format("User with id = %d not found", Long.MAX_VALUE));
    }

    @Test
    void getItemRequestsByOther() {
        NewItemRequestDto newItemRequestDto2 = new NewItemRequestDto("description2");

        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);
        ItemRequestDto itemRequestDto2 = itemRequestService.createItemRequest(user2.getId(), newItemRequestDto2);
        List<ItemRequestWithItemDto> itemRequestList = itemRequestService.getItemRequestsByOther(user.getId());

        assertNotNull(itemRequestList);
        assertEquals(1, itemRequestList.size());
        assertEquals(itemRequestDto2.getDescription(), itemRequestList.get(0).getDescription());
    }

    @Test
    void getItemRequestsByValidId() {
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);

        ItemRequestWithItemDto itemRequest = itemRequestService.getItemRequestsById(itemRequestDto.getId());

        assertNotNull(itemRequest);
        assertNotNull(itemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    void getItemRequestsByNotValidId() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsById(Long.MAX_VALUE));

        assertEquals(notFoundException.getMessage(), String.format("ItemRequest with id = %d not found", Long.MAX_VALUE));
    }
}