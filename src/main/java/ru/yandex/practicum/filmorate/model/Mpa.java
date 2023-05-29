package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Mpa {

    @NotNull
    private Long id;
    @NotNull
    private String name;

}
