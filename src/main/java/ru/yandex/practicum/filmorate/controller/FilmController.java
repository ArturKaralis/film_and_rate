package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Создается фильм: {}", film);
        return filmService.createFilm(film);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Обновляется фильм: {}", film);
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilmById(@PathVariable("filmId") long id) {
        filmService.deleteFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Начинаем ставить лайк фильму с ид {} пользователем с ид {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Добавлен лайк фильму с ид {} пользователем с ид {}: {}", id, userId, filmService.getFilmById(id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Начинаем удалять лайк у фильма с ид {} пользователя с ид {}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Удален лайк фильма с ид {} пользователя с ид {}: {}", id, userId, filmService.getFilmById(id));
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(@RequestParam(defaultValue = "10") @Positive int  count) {
        log.info("Получение списка первых {} фильмов", count);
        return filmService.getTopFilms(count);
    }
}
