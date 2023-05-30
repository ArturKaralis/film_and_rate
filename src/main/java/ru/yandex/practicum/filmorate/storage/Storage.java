package ru.yandex.practicum.filmorate.storage;


import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    List<T> getAll();

    Optional<T> getById(long id);

    T create(T object);

    Optional<T> update(T object);

    T delete(long id);
}
