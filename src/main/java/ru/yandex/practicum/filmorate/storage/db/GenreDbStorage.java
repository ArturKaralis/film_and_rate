package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(Long id) {
        String sqlQuery =
                "SELECT GENRE_ID, GENRE_NAME as genreName " +
                        "FROM Genres " +
                        "WHERE GENRE_ID = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (RuntimeException e) {
            throw new ObjectNotFoundException("Жанр не найден.", id);
        }
    }

    public List<Genre> findAll() {
        List<Genre> genreList = new ArrayList<>();

        String sqlQuery =
                "SELECT GENRE_ID, GENRE_NAME " +
                        "FROM Genres";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.valueOf(resultSet.getString("GENRE_NAME"));
    }

}
