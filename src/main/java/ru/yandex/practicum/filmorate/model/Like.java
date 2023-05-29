package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Like {

    @NotNull
    private Long id;
    @NotNull
    @Positive
    private Long userId;
    @NotNull
    @Positive
    private Long filmId;
    @NotNull
    private Long timestamp;

}
