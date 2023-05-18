package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> storageFilms = new HashMap();
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
        log.info("Запрошен фильм с id '{}' ", id);
        return storageFilms.get(id);
    }

    @Override
    public Film create(Film film) {
         if (film != null) {
            film.setId(generateId());
            storageFilms.put(film.getId(), film);
            log.info("Фильм '{}' с id '{}' был успешно добавлен.", film.getName(), film.getId());
            return film;
         }
         return null;
    }

    @Override
    public Film update(Film film) {
        storageFilms.put(film.getId(), film);
        log.info("Фильм '{}' с id '{}' был успешно обновлён.", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film delete(long id) {
        log.info("Запрошено удаление фильма с id '{}'", id);
        return storageFilms.remove(id);
    }
}
