package ru.practicum.shareit.request;

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
import static ru.practicum.shareit.StaticMethodsAndStringsForTests.makeRequestItemDto;
import static ru.practicum.shareit.StaticMethodsAndStringsForTests.makeRequestRequestDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class RequestServiseTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    @DisplayName("Создание запроса")
    void createItem() {
        RequestUserDto requestUserDto = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
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
        RequestUserDto requestUserDto = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        RequestRequestDto requestRequestDto1 = makeRequestRequestDto("Нужно лопата, которая копает снег");
        RequestRequestDto requestRequestDto2 = makeRequestRequestDto("Нужна дрель, которая сверлит стены");
        RequestRequestDto requestRequestDto3 = makeRequestRequestDto("Нужна ружье, которое стреляет в белок");

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
        RequestUserDto requestUserDto = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        RequestRequestDto requestRequestDto1 = makeRequestRequestDto("Нужно лопата, которая копает снег");
        RequestRequestDto requestRequestDto2 = makeRequestRequestDto("Нужна дрель, которая сверлит стены");
        RequestRequestDto requestRequestDto3 = makeRequestRequestDto("Нужна ружье, которое стреляет в белок");

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
        RequestUserDto requestUserDto = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        RequestRequestDto requestRequestDto = makeRequestRequestDto("Нужно лопата, которая копает снег");

        ResponseUserDto responseUserDto = userService.createUser(requestUserDto);
        long authorId = responseUserDto.getId();
        ResponseRequestDto responseRequestDto = requestService.createItemRequest(authorId, requestRequestDto);
        long requestId = responseRequestDto.getId();

        ResponseRequestDto findRequest = requestService.getInformationAboutOneRequest(authorId, requestId);
        assertThat(findRequest.getId(), notNullValue());
        assertThat(findRequest.getCreated(), notNullValue());
        assertThat(findRequest.getItems().size(), equalTo(0));
        assertThat(findRequest.getDescription(), equalTo(requestRequestDto.getDescription()));
    }

    @Test
    @DisplayName("Поиск информации о конкретном запросе с вещами")
    void getInformationAboutOneRequestWithItems() {
        RequestUserDto requestUserDto1 = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long authorId1 = responseUserDto1.getId();
        RequestRequestDto requestRequestDto = makeRequestRequestDto("Нужно лопата, которая копает снег");
        ResponseRequestDto responseRequestDto = requestService.createItemRequest(authorId1, requestRequestDto);
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
        assertThat(findRequest.getDescription(), equalTo(requestRequestDto.getDescription()));
    }

    private RequestUserDto makeRequestUserDto(String name, String email) {
        RequestUserDto dto = new RequestUserDto();
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }
}
