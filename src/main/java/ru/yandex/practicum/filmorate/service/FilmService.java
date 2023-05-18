package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        log.info("Фильм '{}' с id '{}' был успешно добавлен.", film.getName(), film.getId());
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()) == null) {
            log.warn("Запрос на обновление фильма с id '{}' отклонён. Он отсутствует в списке фильмов.", film.getId());
            throw new ObjectNotFoundException(film.getName(), film.getId());
        }
        validateFilm(film);
        filmStorage.update(film);
        return film;
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new ObjectNotFoundException("Фильм", id);
        }
        return film;
    }

    public Film deleteFilmById(long id) {
        if (filmStorage.delete(id) != null) {
            return filmStorage.delete(id);
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
        Set<Long> likes = film.getLikes();
        likes.add(userId);
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
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> allFilms = filmStorage.getAll();
        return allFilms.stream()
                .sorted((a, b) -> b.getRate() - a.getRate())
                .limit(count)
                .collect(Collectors.toList());
    }
}
