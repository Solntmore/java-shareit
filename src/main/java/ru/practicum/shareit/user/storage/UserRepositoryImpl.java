package ru.practicum.shareit.user.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.user.exceptions.UniquenessEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private final UserRepository userRepository;

    public UserRepositoryImpl(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User patchUser(long userId, User updateUser) {
        User user = userRepository.findById(userId).get();

        Optional<String> optionalName = Optional.ofNullable(updateUser.getName());
        Optional<String> optionalEmail = Optional.ofNullable(updateUser.getEmail());

        if (optionalEmail.isPresent()) {

            String email = optionalEmail.get();
            setEmailIfEmailNotExist(email, user);
        }

        optionalName.ifPresent(user::setName);
        userRepository.save(user);
        return user;
    }

    private void setEmailIfEmailNotExist(String email, User user) {
        if (userRepository.existsByEmail(email)) {
            throw new UniquenessEmailException("This email is already registered, try another one");
        }
        user.setEmail(email);
    }
}
