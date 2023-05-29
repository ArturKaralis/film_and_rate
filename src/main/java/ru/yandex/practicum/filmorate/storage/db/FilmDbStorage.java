package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository("filmStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = genreService;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT F.FILM_ID, " +
                "F.FILM_NAME, " +
                "F.DESCRIPTION, " +
                "F.RELEASE_DATE, " +
                "F.DURATION, " +
                "F.MPA_ID, " +
                "M.MPA_NAME " +
                "FROM FILMS AS F " +
                "JOIN MPA_RATINGS AS M ON M.MPA_ID = F.MPA_ID;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService));
    }

    @Override
    public Film getById(long id) throws ValidationException {
        String sqlQuery = "SELECT F.FILM_ID, " +
                "F.FILM_NAME, " +
                "F.DESCRIPTION, " +
                "F.RELEASE_DATE, " +
                "F.DURATION, " +
                "F.MPA_ID, " +
                "M.MPA_NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA_RATINGS AS M ON M.MPA_ID = F.MPA_ID " +
                "WHERE F.FILM_ID = ?;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ValidationException("Фильм с id=" + id + " не существует"));
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setDouble(4, film.getDuration());
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS " +
                "SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE FILM_ID = ?;";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return getById(film.getId());
    }

    @Override
    public Film delete(long filmId) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
        return null;
    }

    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "INSERT INTO LIKES_FILMS (USER_ID, FILM_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM LIKES_FILMS WHERE FILM_ID = ? AND USER_ID = ?;";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    private Film makeFilm(ResultSet rs, GenreService genreService
                          ) throws SQLException {
        long id = rs.getLong("FILM_ID");
        String name = rs.getString("FILM_NAME");
        String description = rs.getString("DESCRIPTION");
        LocalDate releaseDate = LocalDate.parse(rs.getDate("RELEASE_DATE").toString());
        double duration = rs.getDouble("DURATION");
        Genre genre = genreService.getGenreById(id);
        Mpa mpa = new Mpa(
                rs.getLong("MPA_ID"),
                rs.getString("MPA_NAME")
        );
        Film film = new Film(id, name, description, releaseDate, duration, mpa);
        film.getGenres().addAll(Collections.singleton(genre));
        return film;
    }

}
