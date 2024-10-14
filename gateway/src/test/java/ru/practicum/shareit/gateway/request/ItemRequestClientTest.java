package ru.practicum.shareit.gateway.request;

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
import ru.practicum.shareit.gateway.request.dto.NewItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {
    private final String serverUrl = "http://localhost:9090/requests";
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private String body;
    private NewItemRequestDto newItemRequestDto;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        newItemRequestDto = new NewItemRequestDto("text");
        body = objectMapper.writeValueAsString(newItemRequestDto);
    }

    @Test
    @SneakyThrows
    void createItemRequest() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemRequestClient.createItemRequest(userId, newItemRequestDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getItemRequestsByUserId() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemRequestClient.getItemRequestsByUserId(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getItemRequestsByOther() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/all"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemRequestClient.getItemRequestsByOther(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getItemRequestsById() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(userId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = itemRequestClient.getItemRequestsById(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }
}