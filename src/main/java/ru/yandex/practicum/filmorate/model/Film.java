package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
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

    private Mpa mpa;
    private Set<Genre> genres;
    @JsonIgnore
    private Set<Long> likes;

    public Film(long id, String name, String description, LocalDate releaseDate, double duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public int getRate() {
        return likes.size();
    }


    public Set<Long> getLikes() {
        return likes;
    }

    public Set<Genre> setGenres() {
        return genres;
    }
}
