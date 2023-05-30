package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private static User userOne;
    private static User userTwo;
    private static Film filmOne;
    private static Film filmTwo;

    @BeforeEach
    void setUp() {
        userOne = new User(0,
                "email@email.ru",
                "loginOne",
                "nameOne",
                LocalDate.of(1990, 12, 12));
        userTwo = new User(0,
                "yandex@yandex.ru",
                "loginTwo",
                "nameTwo",
                LocalDate.of(1995, 5, 5));
        filmOne = new Film(0,
                "filmOne",
                "descriptionOne",
                LocalDate.of(1949, 1, 1),
                100,
                Mpa.G);
        filmTwo = new Film(0,
                "filmTwo",
                "descriptionTwo",
                LocalDate.of(1977, 7, 7),
                200,
                Mpa.NC17);
    }

    @Test
    public void testGetGenres() {
        assertEquals(genreStorage.findAll().size(), 6);
    }

    @Test
    public void testGetGenreById() {
        assertEquals(genreStorage.getGenreById(1L), Genre.COMEDY);
        assertEquals(genreStorage.getGenreById(2L), Genre.DRAMA);
        assertEquals(genreStorage.getGenreById(3L), Genre.CARTOON);
        assertEquals(genreStorage.getGenreById(4L), Genre.THRILLER);
        assertEquals(genreStorage.getGenreById(5L), Genre.DOCUMENTARY);
        assertEquals(genreStorage.getGenreById(6L), Genre.ACTION);
    }

    @Test
    void testGetMpas() {
        assertEquals(mpaStorage.getAll().size(), 5);
    }

    @Test
    void testGetMpaById() {
        assertEquals(mpaStorage.getById(1L), Mpa.G);
        assertEquals(mpaStorage.getById(2L), Mpa.PG);
        assertEquals(mpaStorage.getById(3L), Mpa.PG13);
        assertEquals(mpaStorage.getById(4L), Mpa.R);
        assertEquals(mpaStorage.getById(5L), Mpa.NC17);
    }

    @Test
    void testEmptyGetFilms() {
        Optional<List<Film>> films = filmStorage.getAll();

        assertTrue(films.isPresent());
        assertTrue(films.get().isEmpty());
    }

    @Test
    void testAddFilm() {
        Optional<Film> filmOptional = filmStorage.create(filmOne);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(film).hasFieldOrPropertyWithValue("name", "filmOne");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "descriptionOne");
                    assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1));
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 100D);
                    assertThat(film).hasFieldOrPropertyWithValue("mpa", Mpa.G);
                });
    }

    @Test
    void testGetFilms() {
        filmStorage.create(filmOne);
        Optional<List<Film>> filmsOptional = filmStorage.getAll();

        assertTrue(filmsOptional.isPresent());
        assertEquals(filmsOptional.get().size(), 1);
        assertThat(filmsOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "filmOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "descriptionOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1));
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("duration", 100D);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("mpa", Mpa.G);
                });

        filmStorage.create(filmTwo);
        filmsOptional = filmStorage.getAll();

        assertTrue(filmsOptional.isPresent());
        assertEquals(filmsOptional.get().size(), 2);
        assertThat(filmsOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "filmOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "descriptionOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1));
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("duration", 100D);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("mpa", Mpa.G);

                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", "filmTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("description", "descriptionTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1977, 7, 7));
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("duration", 200D);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("mpa", Mpa.NC17);
                });
    }

    @Test
    void testUpdateFilm() {
        filmStorage.create(filmOne);
        filmTwo.setId(1);
        filmStorage.update(filmTwo);
        Optional<List<Film>> filmsOptional = filmStorage.getAll();

        assertTrue(filmsOptional.isPresent());
        assertEquals(filmsOptional.get().size(), 1);
        assertThat(filmsOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "filmTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "descriptionTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1977, 7, 7));
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("duration", 200D);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("mpa", Mpa.NC17);
                });
    }

    @Test
    void testEmptyGetUsers() {
        Optional<List<User>> usersOptional = userStorage.getAll();

        assertTrue(usersOptional.isPresent());
        assertTrue(usersOptional.get().isEmpty());
    }

    @Test
    void testAddUser() {
        Optional<User> userOptional = userStorage.create(userOne);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(user).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(user).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(user).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));
                });
    }

    @Test
    void testGetUsers() {
        userStorage.create(userOne);
        Optional<List<User>> usersOptional = userStorage.getAll();

        assertTrue(usersOptional.isPresent());
        assertEquals(usersOptional.get().size(), 1);
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));
                });

        userStorage.create(userTwo);
        usersOptional = userStorage.getAll();

        assertTrue(usersOptional.isPresent());
        assertEquals(usersOptional.get().size(), 2);
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));

                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void testUpdateUser() {
        userStorage.create(userOne);
        userTwo.setId(1);
        userStorage.update(userTwo);
        Optional<List<User>> usersOptional = userStorage.getAll();

        assertTrue(usersOptional.isPresent());
        assertEquals(usersOptional.get().size(), 1);
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void testAddFriend() {
        userStorage.create(userOne);
        userStorage.create(userTwo);

        Optional<List<User>> friendsUserOne = userStorage.makeFriends(1L, 2L);

        assertTrue(friendsUserOne.isPresent());
        assertEquals(friendsUserOne.get().size(), 1);
        assertThat(friendsUserOne)
                .isPresent()
                .hasValueSatisfying(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });

        Optional<List<User>> friendsUserTwo = userStorage.getUserFriendsById(2L);

        assertTrue(friendsUserTwo.isPresent());
        assertTrue(friendsUserTwo.get().isEmpty());
    }

    @Test
    void testRemoveFriend() {
        userStorage.create(userOne);
        userStorage.create(userTwo);
        userStorage.makeFriends(1L, 2L);

        Optional<List<User>> friendsUserOne = userStorage.removeFriends(1L, 2L);

        assertTrue(friendsUserOne.isPresent());
        assertTrue(friendsUserOne.get().isEmpty());
    }
}
