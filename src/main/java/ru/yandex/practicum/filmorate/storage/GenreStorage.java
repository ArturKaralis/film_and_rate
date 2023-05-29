package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenreById(Long id);

    List<Genre> findAll();

    void addGenresForCurrentFilm(Film film);

    void addAllToFilmId(Long filmId, List<Genre> genre);

    void deleteAllByFilmId(Long filmId);
}
