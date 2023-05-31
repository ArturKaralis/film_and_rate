package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("filmStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public List<Film> getAll() {
        String sqlQuery = "SELECT F.FILM_ID AS FILM_ID, " +
                "F.FILM_NAME AS FILM_NAME, " +
                "F.DESCRIPTION AS DESCRIPTION, " +
                "F.RELEASE_DATE AS RELEASE_DATE, " +
                "F.DURATION AS DURATION, " +
                "F.MPA_ID AS MPA_ID, " +
                "M.MPA_NAME as MPA_NAME " +
                "FROM FILMS AS F " +
                "LEFT JOIN MPA_RATINGS AS M ON M.MPA_ID = F.MPA_ID ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    @Transactional
    public Film getById(long id) {
        String sqlQuery = "SELECT F.*, " +
                "M.MPA_NAME as MPA_NAME " +
                "FROM FILMS AS F " +
                "LEFT JOIN MPA_RATINGS AS M ON M.MPA_ID = F.MPA_ID " +
                "WHERE F.FILM_ID = ?;";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    @Transactional
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO FILMS (" +
                "FILM_NAME, " +
                "DESCRIPTION, " +
                "RELEASE_DATE, " +
                "DURATION, " +
                "MPA_ID) " +
                "VALUES (?,?,?,?,?)";
        KeyHolder id = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setDouble(4, film.getDuration());
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, id);
        film.setId(Objects.requireNonNull(id.getKey()).intValue());
        return film;
    }

    @Override
    @Transactional
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS " +
                "SET FILM_NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ?, " +
                "MPA_ID = ? " +
                "WHERE FILM_ID = ?;";
        try {
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
        } catch (DataAccessException e) {
            return film;
        }
        return film;
    }

    @Override
    @Transactional
    public void delete(long filmId) {
        String sqlQuery = "DELETE FROM FILMS " +
                "WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    @Transactional
    public List<Film> getTopFilms(int count) {
        String sqlQuery = "SELECT F.*, " +
                "RM.MPA_NAME as MPA_NAME " +
                "FROM FILMS AS F " +
                "LEFT JOIN MPA_RATINGS AS RM ON f.MPA_ID = rm.MPA_ID " +
                "LEFT JOIN LIKES_FILMS AS l ON F.FILM_ID = l.FILM_ID " +
                "GROUP BY f.FILM_ID ORDER BY COUNT(l.USER_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    @Transactional
    public void isFilmExisted(long id) {
        String sqlQuery = "SELECT FILM_ID " +
                "FROM FILMS " +
                "WHERE FILM_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowSet.next()) {
            throw new ObjectNotFoundException("Фильм с id: " + id + " не найден", id);
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(rs.getLong("MPA_ID"), rs.getString("MPA_NAME"));
        return new Film(rs.getLong("FILM_ID"),
                rs.getString("FILM_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getDouble("DURATION"),
                mpa,
                new LinkedHashSet<>()
        );
    }
}