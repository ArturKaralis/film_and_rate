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

        Film film = new Film(1, "New film", "New Description", LocalDate.of(3000, 8, 1), 4, mpa, null);

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

        Film film = new Film(1, "Update film", "Update Description", LocalDate.of(3001, 8, 1), 4, mpa, null);
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

        Film film = new Film(1, "New film", "New Description", LocalDate.of(3001, 8, 1), 4, mpa, null);
        film.setMpa(mpa);
        film.setMpa(mpa);

        filmStorage.create(film);


        List<Film> films = filmStorage.getAll();

        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(2);
    }

}
