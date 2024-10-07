package ru.practicum.shareit.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.item.dto.NewCommentDto;
import ru.practicum.shareit.gateway.item.dto.NewItemDto;
import ru.practicum.shareit.gateway.item.dto.UpdateItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> create(Long ownerId, NewItemDto newItemDto) {
        return post("", ownerId, newItemDto);
    }

    public ResponseEntity<Object> update(Long itemId, Long userId, UpdateItemDto updateItemDto) {
        return patch("/%d".formatted(itemId), userId, updateItemDto);
    }

    public ResponseEntity<Object> getAllByUserId(Long userId) {
        return get("/%d".formatted(userId));
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/%d".formatted(itemId), userId);
    }

    public ResponseEntity<Object> getBySearch(Long userId, String text) {
        Map<String, Object> param = Map.of("text", text);
        return get("/search?text={text}", userId, param);
    }

    public ResponseEntity<Object> createComment(Long itemId, Long userId, NewCommentDto newCommentDto) {
        return post("/%d/comment".formatted(itemId), userId, newCommentDto);
    }
}
