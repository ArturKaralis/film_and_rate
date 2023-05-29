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
                "SELECT id, name " +
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
                "SELECT id, name " +
                        "FROM Genres");

        while (genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(genreRows.getLong("genreId"))
                    .name(genreRows.getString("name"))
                    .build();
            genreList.add(genre);
        }
        return genreList;
    }

    public Set<Genre> getGenreForCurrentFilm(int id) {
        Set<Genre> genreSet = new TreeSet<>();

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT filmId, genreId " +
                        "FROM FilmGenres " +
                        "ORDER BY genreId ASC");

        while (genreRows.next()) {
            if (genreRows.getLong("filmId") == id) {
                genreSet.add(getGenreById(genreRows.getLong("id")));
            }
        }
        return genreSet;
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

    @Override
    public void addAllToFilmId(Long filmId, List<Genre> genre) {

    }

    @Override
    public void deleteAllByFilmId(Long filmId) {

    }

    public void updateGenresForCurrentFilm(Film film) {
        String sqlQuery =
                "DELETE " +
                        "FROM FilmGenres " +
                        "WHERE filmId = ?";

        jdbcTemplate.update(sqlQuery, film.getId());
        addGenresForCurrentFilm(film);
    }

    public void addGenreNameToFilm(Film film) {
        if (Objects.isNull(film.getGenres())) {
            return;
        }
        film.getGenres().forEach(g -> g.setName(getGenreById(g.getId()).getName()));
    }


    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
