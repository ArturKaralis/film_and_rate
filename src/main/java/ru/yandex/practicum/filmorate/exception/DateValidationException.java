package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DateValidationException extends RuntimeException {
    @Getter
    private final String description;
}
