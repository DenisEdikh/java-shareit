package ru.practicum.shareit.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.gateway.item.dto.NewCommentDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;
import ru.practicum.shareit.gateway.item.dto.UpdateItemDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemClient.class)
class ItemClientTest {
    private final String serverUrl = "http://localhost:9090/items";
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private final NewItemDto newItemDto = new NewItemDto(
            "name",
            "description",
            Boolean.TRUE,
            1L
    );
    private final UpdateItemDto updateItemDto = new UpdateItemDto(
            "name2",
            "description2",
            Boolean.TRUE
    );
    private final NewCommentDto newCommentDto = new NewCommentDto("text");
    private String body;
    private String updateBody;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        body = objectMapper.writeValueAsString(newItemDto);
        updateBody = objectMapper.writeValueAsString(updateItemDto);
    }

    @Test
    @SneakyThrows
    void create() {
        Long ownerId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemClient.create(ownerId, newItemDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void update() {
        Long userId = 1L;
        Long itemId = 1L;
        mockServer.expect(requestTo(serverUrl + "/%d".formatted(itemId)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(updateBody)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemClient.update(itemId, userId, updateItemDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, updateBody);
    }

    @Test
    @SneakyThrows
    void getAllByUserId() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(userId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemClient.getAllByUserId(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getItemById() {
        Long userId = 1L;
        Long itemId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(itemId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemClient.getItemById(itemId, userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getBySearch() {
        Long userId = 1L;
        String search = "name";

        mockServer.expect(requestTo(serverUrl + "/search?text=%s".formatted(search)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemClient.getBySearch(userId, search);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }


    @Test
    @SneakyThrows
    void createComment() {
        Long userId = 1L;
        Long itemId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d/comment".formatted(itemId)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemClient.createComment(itemId, userId, newCommentDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }
}