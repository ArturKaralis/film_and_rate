package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(Long id) {
        String sqlQuery =
                "SELECT GENRE_ID, GENRE_NAME " +
                        "FROM GENRES " +
                        "WHERE GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (DataAccessException e) {
            throw new EmptyResultDataAccessException("Жанр не найден.", Math.toIntExact(id));
        }
    }

    public List<Genre> findAll() {
        String sqlQuery =
                "SELECT GENRE_ID, GENRE_NAME " +
                        "FROM GENRES";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    @Transactional
    public void createFilmGenre(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO GENRES_FILMS (FILM_ID, GENRE_ID) " +
                "VALUES(?,?)";
        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
    }

    @Override
    @Transactional
    public void updateFilmGenre(Film film) {
        String sqlQueryGenres = "DELETE FROM GENRES_FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQueryGenres, film.getId());
        this.createFilmGenre(film);
    }

    @Override
    @Transactional
    public void loadGenres(List<Film> films) {
        final Map<Long, Film> ids = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "SELECT *" +
                " FROM GENRES G, " +
                "GENRES_FILMS FG" +
                " WHERE FG.GENRE_ID = G.GENRE_ID AND FG.FILM_ID in (" + inSql + ")";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            if (!rs.wasNull()) {
                final Film film = ids.get(rs.getLong("FILM_ID"));

                film.createGenre(new Genre(rs.getLong("GENRE_ID"), rs.getString("GENRE_NAME")));
            }
            }, films.stream().map(Film::getId).toArray());
    }


    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getLong("GENRE_ID"),
                resultSet.getString("GENRE_NAME"));
    }

}
