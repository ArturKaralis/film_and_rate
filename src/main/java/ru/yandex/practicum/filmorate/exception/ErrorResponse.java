package ru.yandex.practicum.filmorate.exception;


import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class ErrorResponse {
    private  String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
    public String getError() {
        return error;
    }

}
