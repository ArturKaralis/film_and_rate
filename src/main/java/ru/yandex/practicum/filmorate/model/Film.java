package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Film {

    @PositiveOrZero
    private long id;
    @NotBlank(message = "Название фильма не может быть null")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания 200 символов")
    @NotNull (message = "Описание не может быть пустым")
    private String description;
    @NotNull (message = "Дата создания фильма не может быть пустой")
    private LocalDate releaseDate;
    @Positive (message = "Длительность фильма должна быть положительной")
    private double duration;
    @NotNull
    private Mpa mpa;
    private LinkedHashSet<Genre> genres;



    public void createGenre(Genre genre) {
        if (genres == null) {
            genres = new LinkedHashSet<>();
        }
        genres.add(genre);

    }
}
