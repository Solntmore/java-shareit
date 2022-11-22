package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Counter;
import ru.practicum.shareit.user.exceptions.InvalidEmailException;
import ru.practicum.shareit.user.exceptions.UniquenessEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserInMemoryStorage implements UserStorage {

    private final HashMap<Long, User> usersMap = new HashMap<>();
    private final Counter counter = new Counter();

    @Override
    public User createUser(User user) {
        if (сheckEmailForUniqueness(user.getEmail())) {
            user.setId(counter.increaseId());
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
            String email = optionalEmail.get();

            if (patternMatches(email)) {

                if (сheckEmailForUniqueness(email)) {
                    user.setEmail(email);
                } else {
                    throw new UniquenessEmailException("This email is already registered, try another one");
                }

            } else {
                throw new InvalidEmailException("Enter the correct email.");
            }
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

    private boolean patternMatches(String emailAddress) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    private boolean сheckEmailForUniqueness(String email) {
        for (User user : usersMap.values()) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }
}
