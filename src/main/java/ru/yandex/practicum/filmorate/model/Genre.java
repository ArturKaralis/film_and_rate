package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = "name")
public class Genre {

    @NotNull
    private Long id;
    @NotNull
    private String name;


}