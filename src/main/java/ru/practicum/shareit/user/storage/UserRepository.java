package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    User patchUser(long userId, User updateUser);

    boolean existsByEmail(String email);
}
