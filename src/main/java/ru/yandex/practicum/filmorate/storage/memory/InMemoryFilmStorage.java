package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.validateFilm;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> storageFilms = new HashMap<>();
    private long uniqId;

    private long generateId() {
        return ++uniqId;
    }

    @Override
    public List<Film> getAll() {
        log.info("Запрошен список всех фильмов.");
        return new ArrayList<>(storageFilms.values());
    }

    @Override
    public Film getById(long id) {
        if (storageFilms.get(id) != null) {
            log.info("Запрошен фильм с id '{}' ", id);
            return storageFilms.get(id);
        }
        return null;
    }

    @Override
    public Film create(Film film) {
            film.setId(generateId());
            storageFilms.put(uniqId, film);
            log.info("Фильм '{}' с id '{}' был успешно добавлен.", film.getName(), film.getId());
            return film;
    }

    @Override
    public Film update(Film film) {
        if (storageFilms.containsKey(film.getId())) {
            storageFilms.put(film.getId(), film);
            log.info("Фильм '{}' с id '{}' был успешно обновлён.", film.getName(), film.getId());
            return film;
        }
        return null;
    }

    @Override
    public Film delete(long id) {
        if (storageFilms.get(id) != null) {
            log.info("Запрошено удаление фильма с id '{}'", id);
            return storageFilms.remove(id);
        }
        return null;
    }
}
