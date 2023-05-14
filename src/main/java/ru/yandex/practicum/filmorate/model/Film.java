package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    @PositiveOrZero
    private long id;
    @NotBlank(message = "Название фильма не может быть null")
    private String name;
    @Size(max = 200)
    @NotNull
    private String description;
    private LocalDate releaseDate;
    @Positive
    private double duration;

    private Set<Long> likes = new HashSet<>();

    public int getRate() {
        return likes.size();
    }

    public Film(long id, String name, String description, LocalDate releaseDate, double duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Set<Long> getLikes() {
        return likes;
    }

}
