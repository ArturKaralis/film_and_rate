package ru.yandex.practicum.filmorate.validatortest;


public class FilmValidatorTest {

    /*@Test
    void shouldNotValidateEmptyNameFilm() {
        Film film = new Film(1, "", "Приключения", LocalDate.of(2022, 12,15), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldValidateFilm() {
        Film film = new Film(1, "Кораблик", "Приключения", LocalDate.of(2022, 12,15), 120);
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDescription201Symbols() {
        Film film = new Film(1, "Кораблик", RandomString.make(201), LocalDate.of(2022, 12,15), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldValidateDescription200Symbols() {
        Film film = new Film(1, "Кораблик", RandomString.make(200), LocalDate.of(2022, 12,15), 120);
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDateBefore18951228() {
        Film film = new Film(1, "Кораблик", "Приключения", LocalDate.of(1895, 12,27), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldValidateDate18951228() {
        Film film = new Film(1, "Кораблик", "Приключения", LocalDate.of(1895, 12,28), 120);
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDurationMinus1() {
        Film film = new Film(1, "Кораблик", "Приключения", LocalDate.of(2022, 12,27), -1);
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDurationZero() {
        Film film = new Film(1, "Кораблик", "Приключения", LocalDate.of(2022, 12,28), 0);
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldValidateDurationPlus1() {
        Film film = new Film(1, "Кораблик", "Приключения", LocalDate.of(2022, 12, 28), 1);
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateNameNull() {
        Film film = new Film(1, null, "Приключения", LocalDate.of(2022, 12, 28), 1);
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDescriptionNull() {
        Film film = new Film(1, "Кораблик", null, LocalDate.of(2022, 12, 28), 1);
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateReleaseDateNull() {
        Film film = new Film(1, "Кораблик", "Приключения", null, 1);
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }*/

}
