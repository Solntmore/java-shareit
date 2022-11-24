package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exceptions.UniquenessEmailException;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserInMemoryStorage implements UserStorage {

    private final Map<Long, User> usersMap = new HashMap<>();
    private long counter = 0;

    @Override
    public User createUser(User user) {
        if (isEmailNotExist(user.getEmail())) {
            user.setId(increaseCounter());
            usersMap.put(user.getId(), user);
            log.debug("Пользователь {} успешно создан.", user);
            return user;
        }
        throw new UniquenessEmailException("This email is already registered, try another one");
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(usersMap.get(userId));
    }

    @Override
    public User updateUser(long userId, User updateUser) {
        User user = usersMap.get(userId);

        Optional<String> optionalName = Optional.ofNullable(updateUser.getName());
        Optional<String> optionalEmail = Optional.ofNullable(updateUser.getEmail());

        if (optionalEmail.isPresent()) {

            @Email
            String email = optionalEmail.get();
            setEmailIfEmailNotExist(email, user);
        }

        optionalName.ifPresent(user::setName);
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUserById(long userId) {
        usersMap.remove(userId);
    }

    @Override
    public Collection<User> findAllUsers() {
        return usersMap.values();
    }

    private void setEmailIfEmailNotExist(String email, User user) {
        if (!isEmailNotExist(email)) {
            throw new UniquenessEmailException("This email is already registered, try another one");
        }
        user.setEmail(email);
    }

    private boolean isEmailNotExist(String email) {
        for (User user : usersMap.values()) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }

    private long increaseCounter() {
        return ++counter;
    }
}
