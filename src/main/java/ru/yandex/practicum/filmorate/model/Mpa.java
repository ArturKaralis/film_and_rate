package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@RequiredArgsConstructor
public class Mpa {

    private final long id;
    private final String name;

}