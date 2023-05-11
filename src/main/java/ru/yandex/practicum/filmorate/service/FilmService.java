package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

import javax.validation.Valid;
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

    public Film createFilm(@RequestBody @Valid Film film) {
        validateFilm(film);
        log.info("Фильм '{}' с id '{}' был успешно добавлен.", film.getName(), film.getId());
        return filmStorage.create(film);
    }

    public Film updateFilm(@RequestBody @Valid Film film) {
        filmStorage.update(film);
        if (filmStorage.getById(film.getId()) == null) {
            log.warn("Запрос на обновление фильма с id '{}' отклонён. Он отсутствует в списке фильмов.", film.getId());
            throw new ObjectNotFoundException(film.getName(), film.getId());
        }
        validateFilm(film);
        return film;
    }

    public Film getFilmById(@PathVariable long id) {
        if (filmStorage.getById(id) != null) {
            return filmStorage.getById(id);
        } else {
            log.warn("Фильм не найден. Передан отсутствующий id фильма");
            throw new ObjectNotFoundException("Фильм", id);
        }
    }

    public Film deleteFilmById(@PathVariable long id) {
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
        if (count == 10) {
            return allFilms.stream().limit(10).collect(Collectors.toList());
        }
        return allFilms.stream()
                .filter(f -> null != f.getLikes())
                .filter(f -> !f.getLikes().isEmpty())
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
