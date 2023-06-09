package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
public class Genre {

    private final long id;
    private final String name;

    public Genre(long id, String name) {
        this.id = id;
        this.name = name;
    }

}
