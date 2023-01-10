package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findItemsWithParameters(long userId, String query, Integer from, Integer size) {
        if (query == null && from == 0 && size == 100) {
            return get("", userId);
        }

        if (query == null) {
            query = "";
        }

            Map<String, Object> parameters = Map.of(
                    "text", query,
                    "from", from,
                    "size", size
            );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findItemById(long userId, long itemId) {
        Map<String, Object> parameters = new HashMap<>();

        return get("/" + itemId, userId, parameters);
    }

    public ResponseEntity<Object> createItem(RequestItemDto requestItemDto, long userId) {
        Map<String, Object> parameters = new HashMap<>();

        return post("", userId, parameters, requestItemDto);
    }

    public ResponseEntity<Object> addCommentToItem(RequestCommentDto requestCommentDto, long itemId, long userId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        return post("/{itemId}/comment", userId, parameters, requestCommentDto);
    }

    public ResponseEntity<Object> updateItem(RequestItemDto newItem, long userId, long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );

        return patch("/{itemId}", userId, parameters, newItem);
    }

}
