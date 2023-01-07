package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.RequestUserDto;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.RESET_IDS;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.makeRequestUserDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerMockMvcIntegrationTest {

    private static RequestUserDto user1;
    private static RequestUserDto user2;
    private static RequestUserDto user3;
    private static RequestUserDto userInvalidEmail;
    private static RequestUserDto userInvalidName;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void makeObjects() {
        user1 = makeRequestUserDto("Олег", "bolshakov2022@yandex.ru");
        user2 = makeRequestUserDto("Максим", "konovalov1992@yandex.ru");
        user3 = makeRequestUserDto("Ева", "eva2017@yandex.ru");
        userInvalidEmail = makeRequestUserDto("Олег", "bolshakovyandex.ru");
        userInvalidName = makeRequestUserDto(" ", "bolshakov2022@yandex.ru");
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    @Sql(statements = RESET_IDS)
    public void createUserGetStatus200() throws Exception {

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Олег"))
                .andExpect(jsonPath("$.email").value("bolshakov2022@yandex.ru"));
    }

    @Test
    @DisplayName("Создание пользователя с не валидным email")
    @Sql(statements = RESET_IDS)
    public void createUserWithInvalidEmailAndGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(userInvalidEmail))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Создание пользователя с не валидным name")
    @Sql(statements = RESET_IDS)
    public void createUserWithInvalidNameAndGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(userInvalidName))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение списка всех пользователей")
    @Sql(statements = RESET_IDS)
    public void getAllUsersAndGetStatus200() throws Exception {

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user3))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user1, user2, user3))));
    }

    @Test
    @DisplayName("Получение пользователя по Id")
    @Sql(statements = RESET_IDS)
    public void getUserByIdAndGetStatus200() throws Exception {

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user3))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Олег"))
                .andExpect(jsonPath("$.email").value("bolshakov2022@yandex.ru"));
    }

    @Test
    @DisplayName("Получение пользователя по несуществующему Id")
    @Sql(statements = RESET_IDS)
    public void getUserByInvalidIdAndGetStatus404() throws Exception {

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        get("/users/552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить пользователя по Id")
    @Sql(statements = RESET_IDS)
    public void deleteUserByIdAndGetStatus200() throws Exception {

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        delete("/users/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удалить пользователя по несуществующему Id")
    @Sql(statements = RESET_IDS)
    public void deleteUserByIdAndGetStatus404() throws Exception {

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        delete("/users/552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное обновление пользователя")
    @Sql(statements = RESET_IDS)
    public void patchUserGetStatus200() throws Exception {

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        patch("/users/1")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Максим"))
                .andExpect(jsonPath("$.email").value("konovalov1992@yandex.ru"));
    }

    @Test
    @DisplayName("Неуспешное пользователя из-за неверного id")
    @Sql(statements = RESET_IDS)
    public void patchUserInvalidIdGetStatus404() throws Exception {

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        patch("/users/552")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешное пользователя из-за не валидного name")
    @Sql(statements = RESET_IDS)
    public void patchUserInvalidNameGetStatus404() throws Exception {

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        patch("/users/1")
                                .content(objectMapper.writeValueAsString(userInvalidName))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }


}
