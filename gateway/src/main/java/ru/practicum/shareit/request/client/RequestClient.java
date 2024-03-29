package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(long userId, RequestRequestDto requestRequestDto) {
        Map<String, Object> parameters = new HashMap<>();

        return post("", userId, parameters, requestRequestDto);
    }

    public ResponseEntity<Object> getMyItemsRequest(long userId) {
        Map<String, Object> parameters = new HashMap<>();

        return get("", userId, parameters);
    }

    public ResponseEntity<Object> getInfoOfRequest(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getInformationAboutOneRequest(long userId, long requestId) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );

        return get("/{requestId}", userId, parameters);
    }
}
