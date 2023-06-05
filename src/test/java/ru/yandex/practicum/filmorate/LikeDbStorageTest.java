package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LikeDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final UserDbStorage userStorage;
    private final LikeDbStorage likeStorage;

    @Test
    @Order(1)
    public void testAddLike() {
        User user = new User(
                1L,
                "test@test.com",
                "test",
                "Name",
                LocalDate.of(1991, 6, 1)
        );

        userStorage.create(user);

        User addedUser = userStorage.getById(1);
        assertThat(addedUser).isNotNull();

        Mpa mpa = mpaStorage.getById(1);
        assertThat(mpa).isNotNull();

        Film film = new Film(1L, "Наименование фильма", "Описание фильма",
                LocalDate.of(2000, 12, 28), 200,
                new Mpa(1L, "G"), new LinkedHashSet<>());

        filmStorage.create(film);
        Film addedFilm = filmStorage.getById(1);
        assertThat(addedFilm).isNotNull();

        likeStorage.addLike(addedFilm.getId(), addedUser.getId());

        Film likedFilm = filmStorage.getById(1);
        assertThat(likedFilm).isNotNull();
    }

    @Test
    @Order(2)
    public void testDeleteLike() {
        User user = userStorage.getById(1);
        assertThat(user).isNotNull();

        Film film = filmStorage.getById(1);
        assertThat(film).isNotNull();

        likeStorage.removeLike(film.getId(), user.getId());

        Film likedFilm = filmStorage.getById(1);
        assertThat(film).isNotNull();
    }
}
