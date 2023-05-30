package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository("filmStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT F.FILM_ID as film_id, " +
                "F.FILM_NAME as film_name, " +
                "F.DESCRIPTION as film_description, " +
                "F.RELEASE_DATE as film_release_date, " +
                "F.DURATION as film_duration, " +
                "M.MPA_NAME as mpa " +
                "FROM FILMS AS F " +
                "JOIN MPA_RATINGS AS M ON M.MPA_ID = F.MPA_ID ";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        Map<Long, Film> mapFilms = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        String sqlQueryGetGenres = "select FILM_ID as id,\n" +
                "       GENRE_NAME as genreName\n" +
                "from GENRES_FILMS GF\n" +
                "    left join GENRES G on GF.GENRE_ID = G.GENRE_ID\n" +
                "order by id";

        List<Map<String, Object>> genresFilms = jdbcTemplate.queryForList(sqlQueryGetGenres);

        genresFilms.forEach(
                t -> mapFilms.get(Long.parseLong(t.get("id").toString())).getGenres().add(
                        Genre.valueOf(t.get("genreName").toString())
                ));

        return films;
    }

    @Override
    @Transactional
    public Optional<Film> getById(long id) {
        String sqlQuery = "SELECT F.FILM_ID as film_id, " +
                "F.FILM_NAME as film_name, " +
                "F.DESCRIPTION as film_description, " +
                "F.RELEASE_DATE as film_release_date, " +
                "F.DURATION as film_duration, " +
                "M.MPA_NAME as mpa " +
                "FROM FILMS AS F " +
                "JOIN MPA_RATINGS AS M ON M.MPA_ID = F.MPA_ID " +
                "WHERE F.FILM_ID = ?;";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
        assert film != null;
        String sqlQueryGetGenres = "SELECT GENRE_NAME as genre " +
                "from GENRES_FILMS " +
                "left join GENRES G on G.GENRE_ID = GENRES_FILMS.GENRE_ID " +
                "where FILM_ID = ? " +
                "order by G.GENRE_ID";

        List<Genre> genresFilms = jdbcTemplate.query(sqlQueryGetGenres, this::mapRowToGenre, id);
        film.setGenres(new LinkedHashSet<>(genresFilms));
        return Optional.of(film);
    }

    @Override
    @Transactional
    public Optional<Film> create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        String sqlQueryAddGenres = "insert into GENRES_FILMS (FILM_ID, GENRE_ID)\n" +
                "values (?, ?)";

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQueryAddGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        film.setId(filmId);
        return Optional.of(film);
    }

    @Override
    @Transactional
    public Optional<Film> update(Film film) {
        String sqlQuery = "UPDATE FILMS " +
                "SET FILM_NAME = ?," +
                "DESCRIPTION = ?," +
                "RELEASE_DATE = ?," +
                "DURATION = ?," +
                "MPA_ID = ? " +
                "WHERE FILM_ID = ?;";
        try {
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()
            );
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        String sqlQueryDeleteGenres = "delete from GENRES_FILMS\n" +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());

        String sqlQueryAddGenres = "insert into GENRES_FILMS (FILM_ID, GENRE_ID)\n" +
                "values (?, ?)";

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQueryAddGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

        return getById(film.getId());
    }


    @Override
    public Film delete(long filmId) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
        return null;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("film_id"),
                rs.getString("film_name"),
                rs.getString("film_description"),
                rs.getDate("film_release_date").toLocalDate(),
                rs.getInt("film_duration"),
                Mpa.valueOf(rs.getString("mpa"))
        );
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.valueOf(resultSet.getString("GENRE_NAME"));
    }
}