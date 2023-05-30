package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
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
    private Set<Genre> genres = new LinkedHashSet<>();

    public Film(long id, String name, String description, LocalDate releaseDate, double duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", name);
        values.put("DESCRIPTION", description);
        values.put("MPA_ID", mpa.getId());
        values.put("RELEASE_DATE", releaseDate);
        values.put("DURATION", duration);

        return values;
    }
}
