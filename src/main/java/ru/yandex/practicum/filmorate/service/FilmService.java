package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final GenreService genreService;

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        Film receivedFilm = filmStorage.create(film);
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

    public Film addLike(long filmId, long userId) {
        User user = userStorage.getById(userId).orElseThrow(() -> new ObjectNotFoundException(
                "Пользователь с ID %s не найден", userId));
        Film film = filmStorage.getById(filmId).orElseThrow(() -> new ObjectNotFoundException(
                "Фильм с ID %s не найден", filmId));
        film.getLikes().add(user.getId());
        filmStorage.update(film);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        User user = userStorage.getById(userId).orElseThrow(() -> new ObjectNotFoundException(
                "Пользователь с ID %s не найден", userId));
        Film film = filmStorage.getById(filmId).orElseThrow(() -> new ObjectNotFoundException(
                "Фильм с ID %s не найден", filmId));
        film.getLikes().remove(user.getId());
        filmStorage.update(film);
        log.info("Пользователь {} удалил лайк с фильма с id={}", userId, film.getId());
        return film;

    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> allFilms = filmStorage.getAll();
        return allFilms.stream()
                .sorted((a, b) -> b.getRate() - a.getRate())
                .limit(count)
                .collect(Collectors.toList());
    }
}
