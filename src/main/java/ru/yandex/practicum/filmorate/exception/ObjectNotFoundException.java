package ru.yandex.practicum.filmorate.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String object, long id) {
        super(object + " с id: " + id + " не найден!");
    }
}
