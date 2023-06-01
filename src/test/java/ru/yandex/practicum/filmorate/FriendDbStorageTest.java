package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FriendDbStorageTest {
    private final UserDbStorage userStorage;
    private final FriendDbStorage friendStorage;

    @Test
    @Order(1)
    public void testAddFriend() {
        User user1 = new User(
                1,
                "test@test.com",
                "test",
                "Name",
                LocalDate.of(1991, 6, 1)
        );
        userStorage.create(user1);
        User user = userStorage.getById((long) 1);
        assertThat(user).isNotNull();

        User user2 = new User(
               2,
                "friend@test.com",
                "friend",
                "Friend",
                LocalDate.of(1993, 6, 1)
        );
        userStorage.create(user2);
        User friend = userStorage.getById(2);
        assertThat(friend).isNotNull();

        friendStorage.addFriend(user.getId(), friend.getId());

        user = userStorage.getById(1);
        assertThat(user).isNotNull();

        friend = userStorage.getById(2);
        assertThat(friend).isNotNull();
    }
}
