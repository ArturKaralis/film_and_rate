package ru.yandex.practicum.filmorate.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmValidator {
    private static final Logger log = LoggerFactory.getLogger(FilmValidator.class);
    private static LocalDate dateRelease = LocalDate.of(1895, 12, 28);

    public static void validateFilm(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(dateRelease)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}
