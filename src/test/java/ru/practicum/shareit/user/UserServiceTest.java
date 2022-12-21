package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.StaticMethodsAndStringsForTests.makeRequestUserDto;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя")
    void createUser() {
        RequestUserDto requestUserDto = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");

        ResponseUserDto responseUserDto = userService.createUser(requestUserDto);

        assertThat(responseUserDto.getId(), notNullValue());
        assertThat(responseUserDto.getName(), equalTo(requestUserDto.getName()));
        assertThat(responseUserDto.getEmail(), equalTo(requestUserDto.getEmail()));

    }

    @Test
    @DisplayName("Поиск пользователя по id")
    void findUserById() {
        RequestUserDto requestUserDto = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        ResponseUserDto responseUserDto = userService.createUser(requestUserDto);

        ResponseUserDto findUserDto = userService.findUserById(responseUserDto.getId());

        assertThat(findUserDto.getId(), notNullValue());
        assertThat(findUserDto.getName(), equalTo("Олег"));
        assertThat(findUserDto.getEmail(), equalTo("bolshakov2022@yandex.ru"));

    }

    @Test
    @DisplayName("Поиск всех пользователей")
    void findAllUsers() {
        RequestUserDto requestUserDto1 = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        RequestUserDto requestUserDto2 = makeRequestUserDto("Максим", "konovalov1992@yandex.ru");
        userService.createUser(requestUserDto1);
        userService.createUser(requestUserDto2);

        List<ResponseUserDto> userList = userService.findAllUsers();

        assertThat(userList.size(), equalTo(2));
        assertThat(userList.get(0).getName(), equalTo("Олег"));
        assertThat(userList.get(0).getEmail(), equalTo("bolshakov2022@yandex.ru"));
        assertThat(userList.get(1).getName(), equalTo("Максим"));
        assertThat(userList.get(1).getEmail(), equalTo("konovalov1992@yandex.ru"));

    }

    @Test
    @DisplayName("Удаление пользователя")
    void DeleteUsersById() {
        RequestUserDto requestUserDto1 = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");

        userService.createUser(requestUserDto1);
        List<ResponseUserDto> userList = userService.findAllUsers();
        assertThat(userList.size(), equalTo(1));

        userService.deleteUserById(userList.get(0).getId());
        userList = userService.findAllUsers();
        assertThat(userList.size(), equalTo(0));

    }

    @Test
    @DisplayName("Обновление пользователя")
    void UpdateUserById() {
        RequestUserDto requestUserDto = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        RequestUserDto updateRequestUserDto = makeRequestUserDto("НеОлег", "BBolshakov2022@yandex.ru");
        ResponseUserDto responseUserDto = userService.createUser(requestUserDto);
        ResponseUserDto updateResponseUserDto = userService.updateUser(responseUserDto.getId(), updateRequestUserDto);

        assertThat(updateResponseUserDto.getId(), notNullValue());
        assertThat(updateResponseUserDto.getName(), equalTo("НеОлег"));
        assertThat(updateResponseUserDto.getEmail(), equalTo("BBolshakov2022@yandex.ru"));

    }

}
