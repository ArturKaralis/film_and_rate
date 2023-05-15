package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    @PositiveOrZero
    private long id;
    @Email
    @NotBlank(message = "Email не может быть null")
    private String email;
    @NotBlank(message = "Логин не может быть null")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$",
            message = "Логин должен быть от 4 до 12 символов, состоять из английских букв разного регистра и цифр, " +
                    "без специальных символов")
    private String login;
    private String name;
    @PastOrPresent
    @NotNull
    private LocalDate birthday;
    @JsonIgnore
    private Set<Long> friends = new HashSet<>();

        public User(long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
