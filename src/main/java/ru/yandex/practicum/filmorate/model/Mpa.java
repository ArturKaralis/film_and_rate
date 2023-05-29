package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Mpa {

    @NotNull
    private Long id;
    @NotNull
    private String name;

}
