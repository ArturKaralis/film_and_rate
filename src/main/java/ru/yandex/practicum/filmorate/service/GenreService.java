package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreDbStorage;

    public Genre getGenreById(Long genreId) {
        Genre genre = genreDbStorage.getGenreById(genreId);
        if (genre == null) {
            throw new ObjectNotFoundException("Жанр фильма", genreId);
        }
        return genre;
    }

    public List<Genre> findAll() {
        return genreDbStorage.findAll();
    }
}