package ru.yandex.practicum.filmorate.validatortest;


import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserValidatorTest {

    @Test
    void shouldValidateEmptyName() {
        User user = new User(1, "gosha@mail.ru", "goshan", "", LocalDate.of(2000, 05,25));
        assertDoesNotThrow(() -> UserValidator.validateUser(user));
    }

}
