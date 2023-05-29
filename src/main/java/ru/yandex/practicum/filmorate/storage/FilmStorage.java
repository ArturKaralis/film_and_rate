package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage extends Storage<Film> {

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);
}
