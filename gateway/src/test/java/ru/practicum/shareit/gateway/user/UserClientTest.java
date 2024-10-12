package ru.practicum.shareit.gateway.user;

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
import ru.practicum.shareit.gateway.user.dto.NewUserDto;
import ru.practicum.shareit.gateway.user.dto.UpdateUserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(UserClient.class)
class UserClientTest {
    private final String serverUrl = "http://localhost:9090/users";
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private String body;
    private NewUserDto newUserDto;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        newUserDto = new NewUserDto("name", "email@email.com");
        body = objectMapper.writeValueAsString(newUserDto);
    }

    @Test
    @SneakyThrows
    void create() {
        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = userClient.create(newUserDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getAll() {
        mockServer.expect(requestTo(serverUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = userClient.getAll();
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void getById() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(userId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = userClient.getById(userId);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void update() {
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto("name2", "email2@email.com");

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(userId)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = userClient.update(userId, updateUserDto);
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody, body);
    }

    @Test
    @SneakyThrows
    void delete() {
        Long userId = 1L;

        mockServer.expect(requestTo(serverUrl + "/%d".formatted(userId)))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> responseEntity = userClient.delete(userId);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNull(responseEntity.getBody());
    }
}