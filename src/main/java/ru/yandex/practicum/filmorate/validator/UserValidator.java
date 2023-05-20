package ru.yandex.practicum.filmorate.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserValidator {
    private static final Logger log = LoggerFactory.getLogger(UserValidator.class);
    private static LocalDate currentDate = LocalDate.now();


    public static void validateUser(User user) {
        if (!StringUtils.hasText(user.getName())) {
            log.info("Пользователь не указал имя");
            user.setName(user.getLogin());
        }
    }
}
