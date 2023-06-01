package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(OrderAnnotation.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    @Order(1)
    public void testAdd() {
        User user = new User(
                (long) 1,
                "test@test.com",
                "test",
                "Name",
                LocalDate.of(1991, 6, 1)
        );

        userStorage.create(user);

        User addedUser = userStorage.getById((long) 1);

        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getId()).isEqualTo(user.getId());
        assertThat(addedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(addedUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(addedUser.getName()).isEqualTo(user.getName());
        assertThat(addedUser.getBirthday()).isEqualTo(user.getBirthday());
    }

    @Test
    @Order(2)
    public void testUpdate() {
        User user = new User(
                (long) 1,
                "update@test.com",
                "update",
                "Update",
                LocalDate.of(1992, 6, 1)
        );

        userStorage.update(user);

        User updatedUser = userStorage.getById((long) 1);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(updatedUser.getName()).isEqualTo(user.getName());
        assertThat(updatedUser.getBirthday()).isEqualTo(user.getBirthday());
    }

    @Test
    @Order(3)
    public void testFindOne() {
        User user = userStorage.getById((long) 1);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
    }

    @Test
    @Order(4)
    public void testFindAll() {
        User user2 = new User(
                (long) 2,
                "friend@test.com",
                "friend",
                "Friend",
                LocalDate.of(1993, 6, 1)
        );
        userStorage.create(user2);

        List<User> users = userStorage.getAll();
        assertThat(users.size()).isEqualTo(2);
    }

}
