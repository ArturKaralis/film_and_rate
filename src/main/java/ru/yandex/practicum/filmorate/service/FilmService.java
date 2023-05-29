package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userStorage;

    private final GenreService genreService;

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        Film receivedFilm = filmStorage.create(film);
        if (film.getGenres() != null) {
            genreService.updateForFilm(receivedFilm.getId(), film.getGenres());
        }
        log.info("Фильм '{}' с id '{}' был успешно добавлен.", film.getName(), film.getId());
        return receivedFilm;
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        validateFilm(film);
        if (film.getGenres() != null) {
            genreService.updateForFilm(film.getId(), film.getGenres());
        }
        return filmStorage.update(film);
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new ObjectNotFoundException("Фильм", id);
        }
        if (film.getGenres() != null) {
            genreService.updateForFilm(film.getId(), film.getGenres());
        }
        return film;
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

    public void addLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        User user = userStorage.getUserById(userId);
        if (film == null || user == null) {
            throw new ValidationException("Фильм с Id " + id + " не найден или пользователь с Id " + userId + " не найден");
        }
        Set<Like> likes = film.getLikes();
        log.info("Фильм с id={} лайкнул пользователь {}", film.getId(), userId);
        Like like = Like.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .filmId(id)
                .build();
        likes.add(like);
    }

    public void deleteLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        User user = userStorage.getUserById(userId);
        if (film == null || user == null) {
            throw new ValidationException("Фильм с Id " + id + " не найден или пользователь с Id " + userId + " не найден");
        }
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        }
        log.info("Пользователь {} удалил лайк с фильма с id={}", userId, film.getId());
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> allFilms = filmStorage.getAll();
        return allFilms.stream()
                .sorted((a, b) -> b.getRate() - a.getRate())
                .limit(count)
                .collect(Collectors.toList());
    }
}
