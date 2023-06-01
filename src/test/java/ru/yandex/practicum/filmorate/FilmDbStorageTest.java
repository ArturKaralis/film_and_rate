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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;

    private final GenreDbStorage genreStorage;

    @Test
    @Order(1)
    public void testAdd_1() {
        Mpa mpa = mpaStorage.getById(1);
        assertThat(mpa).isNotNull();
        Genre genre = genreStorage.getGenreById(1L);

        Film film = new Film(1, "New film", "New Description", LocalDate.of(3000, 8, 1), 4, mpa, genre);

        filmStorage.create(film);
        Film addedFilm = filmStorage.getById(1);

        assertThat(addedFilm).isNotNull();
        assertThat(addedFilm.getId()).isEqualTo(film.getId());
        assertThat(addedFilm.getName()).isEqualTo(film.getName());
        assertThat(addedFilm.getDescription()).isEqualTo(film.getDescription());
        assertThat(addedFilm.getReleaseDate()).isEqualTo(film.getReleaseDate());
        assertThat(addedFilm.getDuration()).isEqualTo(film.getDuration());
        assertThat(addedFilm.getMpa()).isEqualTo(film.getMpa());
    }

    @Test
    @Order(2)
    public void testUpdate_2() {
        Mpa mpa = mpaStorage.getById(2);
        assertThat(mpa).isNotNull();
        Genre genre = genreStorage.getGenreById(1L);

        Film film = new Film(1, "Update film", "Update Description", LocalDate.of(3001, 8, 1), 4, mpa, genre);
        film.setMpa(mpa);

        filmStorage.update(film);
        Film updatedFilm = filmStorage.getById(1);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getId()).isEqualTo(film.getId());
        assertThat(updatedFilm.getName()).isEqualTo(film.getName());
        assertThat(updatedFilm.getDescription()).isEqualTo(film.getDescription());
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(film.getReleaseDate());
        assertThat(updatedFilm.getDuration()).isEqualTo(film.getDuration());
        assertThat(updatedFilm.getMpa()).isEqualTo(film.getMpa());
    }

    @Test
    @Order(3)
    public void testFindOne_3() {
        Film film = filmStorage.getById(1);

        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(1);
    }

    @Test
    @Order(4)
    public void testFindAll_4() {
        Mpa mpa = mpaStorage.getById(1);
        assertThat(mpa).isNotNull();
        Genre genre = genreStorage.getGenreById(1L);

        Film film = new Film(1, "New film", "New Description", LocalDate.of(3001, 8, 1), 4, mpa, genre);
        film.setMpa(mpa);
        film.setMpa(mpa);

        filmStorage.create(film);


        List<Film> films = filmStorage.getAll();

        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(2);
    }

}
/*import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import javax.validation.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    public final UserDbStorage userStorage;
    private Film filmOne;
    private Film filmSecond;
    private Validator validator;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        filmSecond = new Film(1, "Наименование фильма", "Описание фильма",
                LocalDate.of(2000, 12, 28), 200,
                new Mpa(1, "G"), new LinkedHashSet<>());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void afterEach() {
        String sqlQuery =
                "delete from FRIENDSHIPS;" +
                "delete from FILMS;" +
                "delete from USERS;" +
                "delete from LIKES_FILMS;" +
                "delete from GENRES_FILMS;";
        jdbcTemplate.update(sqlQuery);
    }

    @Test
    void shouldCreateFilm() {
        filmOne = new Film(2, "Наименование фильма 2", "Описание фильма 2",
                LocalDate.of(2000, 12, 28), 100,
                new Mpa(1, "G"), new LinkedHashSet<>());
        filmStorage.create(filmOne);
        assertEquals("Наименование фильма 2", filmOne.getName(), "Наименование фильма 2");
        assertEquals("Описание фильма 2",
                filmOne.getDescription(), "Описание фильма 2");
        assertEquals(String.valueOf(100), filmOne.getDuration(), 100.0);
        assertEquals(String.valueOf(LocalDate.of(2000, 12, 28)), filmOne.getReleaseDate(), filmOne.getReleaseDate());
    }

    @Test
    void shouldNotCreateFilmWithEmptyName() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmSecond.setName("");
            Set<ConstraintViolation<Film>> violations = validator.validate(filmSecond);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
        Assertions.assertEquals("name: Название фильма не может быть null", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithDescriptionMoreThan200symbols() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            String description = "Тут большой текст, превышающий 200 символов ААААААААААААААААААААА" +
                    "ААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААА" +
                    "ААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААА" +
                    "АААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААААА" +
                    "ААААААААААААААААААААААААААААААААА";
            filmSecond.setDescription(description);
            Set<ConstraintViolation<Film>> violations = validator.validate(filmSecond);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
        Assertions.assertEquals("description: Максимальная длина описания 200 символов",
                exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithNullReleaseDate() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmSecond.setReleaseDate(null);
            Set<ConstraintViolation<Film>> violations = validator.validate(filmSecond);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
        Assertions.assertEquals("releaseDate: Дата создания фильма не может быть пустой", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmNullDuration() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmSecond.setDuration(0);
            Set<ConstraintViolation<Film>> violations = validator.validate(filmSecond);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
        Assertions.assertEquals("duration: Длительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmNegativeDuration() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmSecond.setDuration(-1);
            Set<ConstraintViolation<Film>> violations = validator.validate(filmSecond);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
        Assertions.assertEquals("duration: Длительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void shouldUpdateFilm() {
        filmStorage.create(filmSecond);
        filmSecond.setName("New name");
        filmSecond.setDescription("New description");
        filmSecond.setReleaseDate((LocalDate.of(2005, 12, 28)));
        filmSecond.setDuration(500);
        assertEquals("New name", filmSecond.getName(), "New name");
        assertEquals("New description", filmSecond.getDescription(), "New description");
        assertEquals(String.valueOf(500), filmSecond.getDuration(), 500.0);
        assertEquals(String.valueOf(LocalDate.of(2005, 12, 28)), filmSecond.getReleaseDate(), filmSecond.getReleaseDate());
    }

    @Test
    void shouldNotUpdateFilmWithEmptyName() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmStorage.create(filmSecond);
            filmSecond.setName("");
            Set<ConstraintViolation<Film>> violations = validator.validate(filmSecond);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
        Assertions.assertEquals("name: Название фильма не может быть null", exception.getMessage());
    }
}*/