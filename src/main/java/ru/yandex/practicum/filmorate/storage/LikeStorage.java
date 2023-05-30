package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {

    List<Film> getTopFilms(int count);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);
}
