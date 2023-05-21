package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IncorrectPathDataException extends RuntimeException {
    @Getter
    private final String description;
}
