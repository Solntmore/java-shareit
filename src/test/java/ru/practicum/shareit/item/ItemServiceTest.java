package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingServise;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class ItemServiceTest {


    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingServise bookingServise;

    private static RequestUserDto requestUserDto1;
    private static RequestUserDto requestUserDto2;
    private static RequestItemDto requestItemDto1;
    private static RequestItemDto requestItemDto2;
    private static RequestItemDto requestItemDto3;
    private static RequestItemDto requestItemDto4;

    @BeforeAll
    public static void makeObjects() {
        requestUserDto1 = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        requestUserDto2 = makeRequestUserDto("Максим", "konovalov1992@yandex.ru");
        requestItemDto1 = makeRequestItemDto("Дрель", "Круто сверлит",
                true, 1);
        requestItemDto2 = makeRequestItemDto("Дрель", "Нормально сверлит",
                true, 4);
        requestItemDto3 = makeRequestItemDto("Лопата", "Копает землю",
                true, 2);
        requestItemDto4 = makeRequestItemDto("Грабли", "Не класть на землю",
                true, 3);
    }

    @Test
    @DisplayName("Создание вещи")
    void createItem() {
        ResponseUserDto responseUserDto = userService.createUser(requestUserDto1);
        ResponseItemDto responseItemDto = itemService.createItem(requestItemDto1, responseUserDto.getId());

        ResponseItemDto findItem = itemService.findItemAndUserById(responseItemDto.getId(), responseUserDto.getId());

        assertThat(findItem.getId(), notNullValue());
        assertThat(findItem.getName(), equalTo(requestItemDto1.getName()));
        assertThat(findItem.getDescription(), equalTo(requestItemDto1.getDescription()));
        assertThat(findItem.getAvailable(), equalTo(requestItemDto1.getAvailable()));
        assertThat(findItem.getRequestId(), equalTo(requestItemDto1.getRequestId()));
    }

    @Test
    @DisplayName("Обновление вещи")
    void updateItem() {
        RequestItemDto updateRequestItemDto = makeRequestItemDto("Молоток", "Бьет гвозди",
                true, 1);

        ResponseUserDto responseUserDto = userService.createUser(requestUserDto1);
        ResponseItemDto responseItemDto = itemService.createItem(requestItemDto1, responseUserDto.getId());
        itemService.updateItem(updateRequestItemDto, responseItemDto.getId(),
                responseUserDto.getId());

        ResponseItemDto findItem = itemService.findItemAndUserById(responseItemDto.getId(), responseUserDto.getId());

        assertThat(findItem.getId(), notNullValue());
        assertThat(findItem.getName(), equalTo(updateRequestItemDto.getName()));
        assertThat(findItem.getDescription(), equalTo(updateRequestItemDto.getDescription()));
        assertThat(findItem.getAvailable(), equalTo(updateRequestItemDto.getAvailable()));
        assertThat(findItem.getRequestId(), equalTo(updateRequestItemDto.getRequestId()));
    }

    @Test
    @DisplayName("Поиск вещи")
    void findItem() {
        ResponseUserDto responseUserDto = userService.createUser(requestUserDto1);
        ResponseItemDto responseItemDto = itemService.createItem(requestItemDto1, responseUserDto.getId());

        ResponseItemDto findItem = itemService.findItemAndUserById(responseItemDto.getId(), responseUserDto.getId());

        assertThat(findItem.getId(), notNullValue());
        assertThat(findItem.getName(), equalTo(requestItemDto1.getName()));
        assertThat(findItem.getDescription(), equalTo(requestItemDto1.getDescription()));
        assertThat(findItem.getAvailable(), equalTo(requestItemDto1.getAvailable()));
        assertThat(findItem.getRequestId(), equalTo(requestItemDto1.getRequestId()));
    }

    @Test
    @DisplayName("Поиск вещи по описанию")
    void findAllItemsWithParameters() {


        ResponseUserDto responseUserDto = userService.createUser(requestUserDto1);
        itemService.createItem(requestItemDto1, responseUserDto.getId());
        itemService.createItem(requestItemDto2, responseUserDto.getId());
        itemService.createItem(requestItemDto3, responseUserDto.getId());
        itemService.createItem(requestItemDto4, responseUserDto.getId());

        List<ResponseItemDto> itemList = itemService.findAllItemsWithParameters(responseUserDto.getId(), "Дрель",
                PageRequest.of(0, 10));
        assertThat(itemList.size(), equalTo(2));

        itemList = itemService.findAllItemsWithParameters(responseUserDto.getId(), "зЕмлю",
                PageRequest.of(0, 10));
        assertThat(itemList.size(), equalTo(2));
    }

    @Test
    @DisplayName("Добавление комментария")
    void addCommentToItem() {

        ResponseUserDto responseUserDto = userService.createUser(requestUserDto1);
        ResponseUserDto bookerDto = userService.createUser(requestUserDto2);
        ResponseItemDto responseItemDto = itemService.createItem(requestItemDto1, responseUserDto.getId());

        RequestBookingDto requestBookingDto = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto.getId(), bookerDto.getId());
        bookingServise.createBooking(requestBookingDto, bookerDto.getId());
        RequestCommentDto requestCommentDto = makeRequestCommentDto("Крутая вещь");

        ResponseCommentDto responseCommentDto = itemService.addCommentToItem(requestCommentDto,
                responseItemDto.getId(), bookerDto.getId());

        assertThat(responseCommentDto.getId(), notNullValue());
        assertThat(responseCommentDto.getText(), equalTo(requestCommentDto.getText()));
    }
}
