package ru.yandex.practicum.filmorate.validatortest;


import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FilmValidatorTest {

    @Test
    void shouldValidateDate18951228() {
        Film film = new Film(1, "Кораблик", "Приключения", LocalDate.of(1895, 12,28), 120);
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

}
