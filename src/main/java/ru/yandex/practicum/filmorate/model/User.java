package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
@EqualsAndHashCode(of = "id")
public class User {

    @PositiveOrZero
    private long id;
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email не корректный")
    @NotBlank
    @Pattern(regexp = "^\\S*",
            message = "Email не может быть null")
    private String email;
    @NotBlank(message = "Логин не может быть null")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$",
            message = "Логин должен быть от 4 до 12 символов, состоять из английских букв разного регистра и цифр, " +
                    "без специальных символов")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть больше текущей даты")
    @NotNull
    private LocalDate birthday;

}
