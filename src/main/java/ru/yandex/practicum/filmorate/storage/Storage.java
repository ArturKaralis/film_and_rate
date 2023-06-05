package ru.yandex.practicum.filmorate.storage;


import java.util.List;

public interface Storage<T> {
    List<T> getAll();

    T getById(long id);

    T create(T object);

    T update(T object);

    void delete(long id);
}