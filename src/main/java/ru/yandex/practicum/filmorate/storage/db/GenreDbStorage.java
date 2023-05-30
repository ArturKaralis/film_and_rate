package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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
                "SELECT id, GENRE_NAME as genreName " +
                        "FROM Genres " +
                        "WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (RuntimeException e) {
            throw new ValidationException("Жанр не найден.");
        }
    }

    public List<Genre> findAll() {
        List<Genre> genreList = new ArrayList<>();

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT ID, GENRE_NAME " +
                        "FROM Genres");

        while (genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(genreRows.getLong("ID"))
                    .name(genreRows.getString("GENRE_NAME"))
                    .build();
            genreList.add(genre);
        }
        return genreList;
    }

    public void addGenresForCurrentFilm(Film film) {
        if (Objects.isNull(film.getGenres())) {
            return;
        }
        String sqlQuery =
                "INSERT " +
                        "INTO FilmGenres(filmId, genreId) " +
                        "VALUES (?, ?)";

        film.getGenres().forEach(g -> jdbcTemplate.update(sqlQuery, film.getId(), g.getId()));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
