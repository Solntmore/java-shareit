package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class RequestServiseTest {

    private static RequestUserDto requestUserDto;
    private static RequestRequestDto requestRequestDto1;
    private static RequestRequestDto requestRequestDto2;
    private static RequestRequestDto requestRequestDto3;
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @BeforeAll
    public static void makeObjects() {
        requestUserDto = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        requestRequestDto1 = makeRequestRequestDto("Нужно лопата, которая копает снег");
        requestRequestDto2 = makeRequestRequestDto("Нужна дрель, которая сверлит стены");
        requestRequestDto3 = makeRequestRequestDto("Нужна ружье, которое стреляет в белок");
    }

    @Test
    @DisplayName("Создание запроса")
    void createItem() {
        RequestRequestDto requestRequestDto = makeRequestRequestDto("Нужно лопата, которая копает снег");

        ResponseUserDto responseUserDto = userService.createUser(requestUserDto);
        ResponseRequestDto responseRequestDto = requestService.createItemRequest(responseUserDto.getId(),
                requestRequestDto);

        assertThat(responseRequestDto.getId(), notNullValue());
        assertThat(responseRequestDto.getCreated(), notNullValue());
        assertThat(responseRequestDto.getDescription(), equalTo(requestRequestDto.getDescription()));
    }

    @Test
    @DisplayName("Поиск своих запросов")
    void getMyItemsRequest() {
        ResponseUserDto responseUserDto = userService.createUser(requestUserDto);
        long userId = responseUserDto.getId();
        requestService.createItemRequest(userId, requestRequestDto1);
        requestService.createItemRequest(userId, requestRequestDto2);
        requestService.createItemRequest(userId, requestRequestDto3);

        List<ResponseRequestDto> requestsList = requestService.getMyItemsRequest(userId);
        assertThat(requestsList.size(), equalTo(3));
        assertThat(requestsList.get(0).getDescription(), equalTo(requestRequestDto1.getDescription()));
        assertThat(requestsList.get(1).getDescription(), equalTo(requestRequestDto2.getDescription()));
        assertThat(requestsList.get(2).getDescription(), equalTo(requestRequestDto3.getDescription()));
    }

    @Test
    @DisplayName("Поиск чужих запросов")
    void getInfoOfRequest() {
        ResponseUserDto responseUserDto = userService.createUser(requestUserDto);
        long userId = responseUserDto.getId();
        requestService.createItemRequest(userId, requestRequestDto1);
        requestService.createItemRequest(userId, requestRequestDto2);
        requestService.createItemRequest(userId, requestRequestDto3);

        List<ResponseRequestDto> requestsList = requestService.getMyItemsRequest(userId);
        assertThat(requestsList.size(), equalTo(3));
        assertThat(requestsList.get(0).getDescription(), equalTo(requestRequestDto1.getDescription()));
        assertThat(requestsList.get(1).getDescription(), equalTo(requestRequestDto2.getDescription()));
        assertThat(requestsList.get(2).getDescription(), equalTo(requestRequestDto3.getDescription()));
    }

    @Test
    @DisplayName("Поиск информации о конкретном запросе без вещей")
    void getInformationAboutOneRequestWithoutItems() {
        ResponseUserDto responseUserDto = userService.createUser(requestUserDto);
        long authorId = responseUserDto.getId();
        ResponseRequestDto responseRequestDto = requestService.createItemRequest(authorId, requestRequestDto1);
        long requestId = responseRequestDto.getId();

        ResponseRequestDto findRequest = requestService.getInformationAboutOneRequest(authorId, requestId);
        assertThat(findRequest.getId(), notNullValue());
        assertThat(findRequest.getCreated(), notNullValue());
        assertThat(findRequest.getItems().size(), equalTo(0));
        assertThat(findRequest.getDescription(), equalTo(requestRequestDto1.getDescription()));
    }

    @Test
    @DisplayName("Поиск информации о конкретном запросе с вещами")
    void getInformationAboutOneRequestWithItems() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto);
        long authorId1 = responseUserDto1.getId();
        ResponseRequestDto responseRequestDto = requestService.createItemRequest(authorId1, requestRequestDto1);
        long requestId = responseRequestDto.getId();


        RequestItemDto requestItemDto1 = makeRequestItemDto("Дрель", "Круто сверлит",
                true, requestId);
        RequestItemDto requestItemDto2 = makeRequestItemDto("Дрель", "Нормально сверлит",
                true, requestId);
        RequestItemDto requestItemDto3 = makeRequestItemDto("Лопата", "Копает землю",
                true, requestId);

        RequestUserDto requestUserDto2 = makeRequestUserDto("Максим", "konovalov1992@yandex.ru");
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long authorId2 = responseUserDto2.getId();

        itemService.createItem(requestItemDto1, authorId2);
        itemService.createItem(requestItemDto2, authorId2);
        itemService.createItem(requestItemDto3, authorId2);

        ResponseRequestDto findRequest = requestService.getInformationAboutOneRequest(authorId1, requestId);
        assertThat(findRequest.getId(), notNullValue());
        assertThat(findRequest.getCreated(), notNullValue());
        assertThat(findRequest.getItems().size(), equalTo(3));
        assertThat(findRequest.getDescription(), equalTo(requestRequestDto1.getDescription()));
    }
}
