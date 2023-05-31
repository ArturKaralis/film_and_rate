package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

import java.util.*;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikeStorage likeStorage;

    public List<Film> getFilms() {
        log.info("GET {} films", filmStorage.getAll().size());
        List<Film> films = filmStorage.getAll();
        if (films.size() != 0) {
             genreStorage.loadGenres(films);
        }
        return films;

    }

    public Film createFilm(Film film) {
        mpaStorage.isMpaExisted(film.getMpa().getId());
        validateFilm(film);
        filmStorage.create(film);
        genreStorage.createFilmGenre(film);
        log.info("Фильм '{}' с id '{}' был успешно добавлен.", film.getName(), film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.isFilmExisted(film.getId());
        validateFilm(film);
        genreStorage.updateFilmGenre(film);
        mpaStorage.isMpaExisted(film.getMpa().getId());
        filmStorage.update(film);
        log.info("Фильм с id '{}' был успешно обновлен.", film.getId());
        return film;
    }

    public Film getFilmById(long id) {
        filmStorage.isFilmExisted(id);
        Film film = filmStorage.getById(id);
        genreStorage.loadGenres(List.of(film));
        return film;
    }

    public void deleteFilmById(long id) {
        Film film = filmStorage.getById(id);
        if (film != null) {
            filmStorage.delete(id);
        } else {
            log.warn("Фильм не найден. Передан отсутствующий id фильма");
            throw new ObjectNotFoundException("Фильм", id);
        }
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film != null) {
            if (userStorage.getById(userId) != null) {
                likeStorage.addLike(filmId, userId);
            } else {
                throw new ObjectNotFoundException("Пользователь c ID=" + userId + " не найден!", userId);
            }
        } else {
            throw new ObjectNotFoundException("Фильм c ID=" + filmId + " не найден!", filmId);
        }
    }

    public void deleteLike(long filmId, long userId) {
        getFilmById(userId);
        getFilmById(filmId);
        likeStorage.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк с фильма с id={}", userId, filmId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }
}
