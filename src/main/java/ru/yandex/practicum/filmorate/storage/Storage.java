package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface Storage<T> {
    List<T> getAll();

    T getById(long id);

    T create(T object);

    T update(T object);

    T delete(long id);
}
