package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.RequestState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServise;
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
public class BookingServiceTest {

    @Autowired
    private BookingServise bookingServise;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private static RequestUserDto requestUserDto1;
    private static RequestUserDto requestUserDto2;
    private static RequestUserDto requestUserDto3;
    private static RequestUserDto requestUserDto4;
    private static RequestItemDto requestItemDto1;
    private static RequestItemDto requestItemDto2;
    private static RequestItemDto requestItemDto3;
    private static RequestItemDto requestItemDto4;

    @BeforeAll
    public static void makeObjects() {
        requestUserDto1 = makeRequestUserDto("????????", "bolshakov2022@yandex.ru");
        requestUserDto2 = makeRequestUserDto("????????????", "konovalov1992@yandex.ru");
        requestUserDto3 = makeRequestUserDto("????????????", "solntsev@yandex.ru");
        requestUserDto4 = makeRequestUserDto("??????", "eva2017@yandex.ru");
        requestItemDto1 = makeRequestItemDto("??????????", "?????????? ??????????????",
                true, 1);
        requestItemDto2 = makeRequestItemDto("??????????", "?????????????????? ??????????????",
                true, 4);
        requestItemDto3 = makeRequestItemDto("????????????", "???????????? ??????????",
                true, 2);
        requestItemDto4 = makeRequestItemDto("????????????", "???? ???????????? ???? ??????????",
                true, 3);
    }

    @Test
    @DisplayName("???????????????? ????????????????????????")
    void createBooking() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long userId1 = responseUserDto1.getId();
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long userId2 = responseUserDto2.getId();

        ResponseItemDto responseItemDto = itemService.createItem(requestItemDto1, userId1);

        RequestBookingDto requestBookingDto = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto.getId(), userId2);
        ResponseBookingDto responseBookingDto = bookingServise.createBooking(requestBookingDto, userId2);

        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), notNullValue());
        assertThat(responseBookingDto.getItem().getName(), equalTo(requestItemDto1.getName()));
        assertThat(responseBookingDto.getBooker().getId(), notNullValue());
        assertThat(responseBookingDto.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    @DisplayName("?????????????????????????? ????????????????????????")
    void patchStatusOfBookingApprove() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long userId1 = responseUserDto1.getId();
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long userId2 = responseUserDto2.getId();

        ResponseItemDto responseItemDto = itemService.createItem(requestItemDto1, userId1);

        RequestBookingDto requestBookingDto = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto.getId(), userId2);
        ResponseBookingDto responseBookingDto = bookingServise.createBooking(requestBookingDto, userId2);
        ResponseBookingDto updateBookingDto = bookingServise.patchStatusOfBooking(responseBookingDto.getId(),
                true, userId1);

        assertThat(updateBookingDto.getId(), notNullValue());
        assertThat(updateBookingDto.getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(updateBookingDto.getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(updateBookingDto.getItem().getId(), notNullValue());
        assertThat(updateBookingDto.getItem().getName(), equalTo(requestItemDto1.getName()));
        assertThat(updateBookingDto.getBooker().getId(), notNullValue());
        assertThat(updateBookingDto.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    @DisplayName("?????????? ?? ????????????????????????")
    void patchStatusOfBookingReject() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long userId1 = responseUserDto1.getId();
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long userId2 = responseUserDto2.getId();

        ResponseItemDto responseItemDto = itemService.createItem(requestItemDto1, userId1);

        RequestBookingDto requestBookingDto = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto.getId(), userId2);
        ResponseBookingDto responseBookingDto = bookingServise.createBooking(requestBookingDto, userId2);
        ResponseBookingDto updateBookingDto = bookingServise.patchStatusOfBooking(responseBookingDto.getId(),
                false, userId1);

        assertThat(updateBookingDto.getId(), notNullValue());
        assertThat(updateBookingDto.getStart(), equalTo(requestBookingDto.getStart()));
        assertThat(updateBookingDto.getEnd(), equalTo(requestBookingDto.getEnd()));
        assertThat(updateBookingDto.getItem().getId(), notNullValue());
        assertThat(updateBookingDto.getItem().getName(), equalTo(requestItemDto1.getName()));
        assertThat(updateBookingDto.getBooker().getId(), notNullValue());
        assertThat(updateBookingDto.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    @DisplayName("?????????????????? ???????????????????? ?? ????????????????????????")
    void getInfoOfBooking() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long userId1 = responseUserDto1.getId();
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long userId2 = responseUserDto2.getId();

        ResponseItemDto responseItemDto = itemService.createItem(requestItemDto1, userId1);

        RequestBookingDto requestBookingDto = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto.getId(), userId2);
        ResponseBookingDto responseBookingDto = bookingServise.createBooking(requestBookingDto, userId2);
        ResponseBookingDto infoOfBookingDto = bookingServise.getInfoOfBooking(responseBookingDto.getId(), userId1);

        assertThat(responseBookingDto.getId(), equalTo(infoOfBookingDto.getId()));
        assertThat(responseBookingDto.getStart(), equalTo(infoOfBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(infoOfBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(infoOfBookingDto.getItem().getId()));
        assertThat(responseBookingDto.getItem().getName(), equalTo(infoOfBookingDto.getItem().getName()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(infoOfBookingDto.getBooker().getId()));
        assertThat(responseBookingDto.getStatus(), equalTo(infoOfBookingDto.getStatus()));
    }

    @Test
    @DisplayName("?????????????????? ???????????????????? ?? ???????? ?????????????????????????? ????????????????????????")
    void getAllBookingsOfUserInStatusAll() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long userId1 = responseUserDto1.getId();
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long userId2 = responseUserDto2.getId();

        ResponseItemDto responseItemDto1 = itemService.createItem(requestItemDto1, userId1);
        ResponseItemDto responseItemDto2 = itemService.createItem(requestItemDto2, userId1);
        ResponseItemDto responseItemDto3 = itemService.createItem(requestItemDto3, userId1);
        ResponseItemDto responseItemDto4 = itemService.createItem(requestItemDto4, userId1);

        RequestBookingDto requestBookingDto1 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto1.getId(), userId2);
        RequestBookingDto requestBookingDto2 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto2.getId(), userId2);
        RequestBookingDto requestBookingDto3 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto3.getId(), userId2);
        RequestBookingDto requestBookingDto4 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto4.getId(), userId2);

        ResponseBookingDto responseBookingDto1 = bookingServise.createBooking(requestBookingDto1, userId2);
        ResponseBookingDto responseBookingDto2 = bookingServise.createBooking(requestBookingDto2, userId2);
        ResponseBookingDto responseBookingDto3 = bookingServise.createBooking(requestBookingDto3, userId2);
        ResponseBookingDto responseBookingDto4 = bookingServise.createBooking(requestBookingDto4, userId2);

        List<ResponseBookingDto> bookingList = bookingServise.getAllBookingsOfUser(RequestState.ALL, userId2,
                PageRequest.of(0, 100));

        assertThat(bookingList.size(), equalTo(4));
        assertThat(bookingList.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(1).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(2).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(3).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(0).getId(), equalTo(responseBookingDto4.getId()));
        assertThat(bookingList.get(1).getId(), equalTo(responseBookingDto3.getId()));
        assertThat(bookingList.get(2).getId(), equalTo(responseBookingDto2.getId()));
        assertThat(bookingList.get(3).getId(), equalTo(responseBookingDto1.getId()));
    }

    @Test
    @DisplayName("?????????????????? ???????????????????? ?? ??????????????????????????, ?????????????????? ??????????????????????????")
    void getAllBookingsOfUserInStatusWaiting() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long userId1 = responseUserDto1.getId();
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long userId2 = responseUserDto2.getId();

        ResponseItemDto responseItemDto1 = itemService.createItem(requestItemDto1, userId1);
        ResponseItemDto responseItemDto2 = itemService.createItem(requestItemDto2, userId1);
        ResponseItemDto responseItemDto3 = itemService.createItem(requestItemDto3, userId1);
        ResponseItemDto responseItemDto4 = itemService.createItem(requestItemDto4, userId1);

        RequestBookingDto requestBookingDto1 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto1.getId(), userId2);
        RequestBookingDto requestBookingDto2 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto2.getId(), userId2);
        RequestBookingDto requestBookingDto3 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto3.getId(), userId2);
        RequestBookingDto requestBookingDto4 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto4.getId(), userId2);

        ResponseBookingDto responseBookingDto1 = bookingServise.createBooking(requestBookingDto1, userId2);
        ResponseBookingDto responseBookingDto2 = bookingServise.createBooking(requestBookingDto2, userId2);
        ResponseBookingDto responseBookingDto3 = bookingServise.createBooking(requestBookingDto3, userId2);
        ResponseBookingDto responseBookingDto4 = bookingServise.createBooking(requestBookingDto4, userId2);
        bookingServise.patchStatusOfBooking(responseBookingDto4.getId(), false, userId1);

        List<ResponseBookingDto> bookingList = bookingServise.getAllBookingsOfUser(RequestState.WAITING, userId2,
                PageRequest.of(0, 100));

        assertThat(bookingList.size(), equalTo(3));
        assertThat(bookingList.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(1).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(2).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(0).getId(), equalTo(responseBookingDto3.getId()));
        assertThat(bookingList.get(1).getId(), equalTo(responseBookingDto2.getId()));
        assertThat(bookingList.get(2).getId(), equalTo(responseBookingDto1.getId()));
    }

    @Test
    @DisplayName("?????????????????? ???????????????????? ?? ???????? ?????????????????????????? ???????? ????????????????????????")
    void getAllBookingsForUsersItemsAll() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long userId1 = responseUserDto1.getId();
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long userId2 = responseUserDto2.getId();
        ResponseUserDto responseUserDto3 = userService.createUser(requestUserDto3);
        long userId3 = responseUserDto3.getId();
        ResponseUserDto responseUserDto4 = userService.createUser(requestUserDto4);
        long userId4 = responseUserDto4.getId();

        ResponseItemDto responseItemDto1 = itemService.createItem(requestItemDto1, userId1);

        RequestBookingDto requestBookingDto1 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto1.getId(), userId2);
        RequestBookingDto requestBookingDto2 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto1.getId(), userId3);
        RequestBookingDto requestBookingDto3 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto1.getId(), userId4);

        ResponseBookingDto responseBookingDto1 = bookingServise.createBooking(requestBookingDto1, userId2);
        ResponseBookingDto responseBookingDto2 = bookingServise.createBooking(requestBookingDto2, userId3);
        ResponseBookingDto responseBookingDto3 = bookingServise.createBooking(requestBookingDto3, userId4);

        List<ResponseBookingDto> bookingList = bookingServise.getAllBookingsForUsersItems(RequestState.ALL, userId1,
                PageRequest.of(0, 100));

        assertThat(bookingList.size(), equalTo(3));
        assertThat(bookingList.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(1).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(2).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(0).getId(), equalTo(responseBookingDto3.getId()));
        assertThat(bookingList.get(1).getId(), equalTo(responseBookingDto2.getId()));
        assertThat(bookingList.get(2).getId(), equalTo(responseBookingDto1.getId()));
        assertThat(bookingList.get(0).getItem().getName(), equalTo(responseBookingDto3.getItem().getName()));
        assertThat(bookingList.get(1).getItem().getName(), equalTo(responseBookingDto2.getItem().getName()));
        assertThat(bookingList.get(2).getItem().getName(), equalTo(responseBookingDto1.getItem().getName()));
    }

    @Test
    @DisplayName("?????????????????? ???????????????????? ?? ???????? ?????????????????????????? ???????? ???????????????????????? ?? ?????????????? ????????????????")
    void getAllBookingsForUsersItemsInStatusWaiting() {
        ResponseUserDto responseUserDto1 = userService.createUser(requestUserDto1);
        long userId1 = responseUserDto1.getId();
        ResponseUserDto responseUserDto2 = userService.createUser(requestUserDto2);
        long userId2 = responseUserDto2.getId();
        ResponseUserDto responseUserDto3 = userService.createUser(requestUserDto3);
        long userId3 = responseUserDto3.getId();
        ResponseUserDto responseUserDto4 = userService.createUser(requestUserDto4);
        long userId4 = responseUserDto4.getId();

        ResponseItemDto responseItemDto1 = itemService.createItem(requestItemDto1, userId1);

        RequestBookingDto requestBookingDto1 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto1.getId(), userId2);
        RequestBookingDto requestBookingDto2 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto1.getId(), userId3);
        RequestBookingDto requestBookingDto3 = makeRequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusNanos(10), responseItemDto1.getId(), userId4);

        ResponseBookingDto responseBookingDto1 = bookingServise.createBooking(requestBookingDto1, userId2);
        ResponseBookingDto responseBookingDto2 = bookingServise.createBooking(requestBookingDto2, userId3);
        ResponseBookingDto responseBookingDto3 = bookingServise.createBooking(requestBookingDto3, userId4);
        bookingServise.patchStatusOfBooking(responseBookingDto1.getId(), false, userId1);

        List<ResponseBookingDto> bookingList = bookingServise.getAllBookingsForUsersItems(RequestState.WAITING, userId1,
                PageRequest.of(0, 100));

        assertThat(bookingList.size(), equalTo(2));
        assertThat(bookingList.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(1).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingList.get(0).getId(), equalTo(responseBookingDto3.getId()));
        assertThat(bookingList.get(1).getId(), equalTo(responseBookingDto2.getId()));
        assertThat(bookingList.get(0).getItem().getName(), equalTo(responseBookingDto3.getItem().getName()));
        assertThat(bookingList.get(1).getItem().getName(), equalTo(responseBookingDto2.getItem().getName()));
    }
}
