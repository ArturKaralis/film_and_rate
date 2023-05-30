package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final LikeStorage likeStorage;

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        Film receivedFilm = filmStorage.create(film).orElseThrow(() -> new ValidationException(
                "Не удалось добавить фильм"
        ));
        log.info("Фильм '{}' с id '{}' был успешно добавлен.", film.getName(), film.getId());
        return receivedFilm;
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        validateFilm(film);
        return filmStorage.update(film).orElseThrow(() -> new ObjectNotFoundException(
                "Фильм с ID %s не найден", film.getId()));
    }

    public Film getFilmById(long id) {
        return filmStorage.getById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Фильм с ID %s не найден", id));
    }

    public Film deleteFilmById(long id) {
        Film film = filmStorage.delete(id);
        if (film != null) {
            return film;
        } else {
            log.warn("Фильм не найден. Передан отсутствующий id фильма");
            throw new ObjectNotFoundException("Фильм", id);
        }
    }

    public void addLike(long filmId, long userId) {
        userStorage.getById(userId).orElseThrow(() -> new ObjectNotFoundException(
                "Пользователь с ID %s не найден", userId));
        filmStorage.getById(filmId).orElseThrow(() -> new ObjectNotFoundException(
                "Фильм с ID %s не найден", filmId));
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        userStorage.getById(userId).orElseThrow(() -> new ObjectNotFoundException(
                "Пользователь с ID %s не найден", userId));
        filmStorage.getById(filmId).orElseThrow(() -> new ObjectNotFoundException(
                "Фильм с ID %s не найден", filmId));
        likeStorage.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк с фильма с id={}", userId, filmId);
    }

    public List<Film> getTopFilms(Integer count) {
        return likeStorage.getTopFilms(count);
    }
}