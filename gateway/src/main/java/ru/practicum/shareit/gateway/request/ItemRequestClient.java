package ru.practicum.shareit.gateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.request.dto.NewItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItemRequest(Long userId, NewItemRequestDto newItemRequestDto) {
        return post("", userId, newItemRequestDto);
    }

    public ResponseEntity<Object> getItemRequestsByUserId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemRequestsByOther(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getItemRequestsById(Long requestId) {
        return get("/%d".formatted(requestId));
    }
}
