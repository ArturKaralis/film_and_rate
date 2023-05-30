package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getTopFilms(int count) {
        String sqlQuery = "select F.FILM_ID as film_id," +
                "       F.DESCRIPTION as film_description," +
                "       F.FILM_NAME as film_name," +
                "       F.RELEASE_DATE as film_release_date," +
                "       F.DURATION as film_duration," +
                "       R.MPA_NAME as mpa " +
                "from FILMS F " +
                "left join MPA_RATINGS R on R.MPA_ID = F.MPA_ID " +
                "left join LIKES_FILMS FL on FL.FILM_ID = F.FILM_ID " +
                "group by F.FILM_ID, " +
                "   F.DESCRIPTION, " +
                "   F.FILM_NAME, " +
                "   F.RELEASE_DATE, " +
                "   F.DURATION, " +
                "   R.MPA_NAME " +
                "order by count(FL.USER_ID) desc " +
                "limit ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);

        if (!films.isEmpty()) {
            Map<Long, Film> mapFilms = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
            List<Long> filmsId = new ArrayList<>(mapFilms.keySet());
            String sqlQueryGetAllGenres = "select FG.FILM_ID as filmId, " +
                    "       G.GENRE_NAME as genreName, " +
                    "       G.GENRE_ID as genreId " +
                    "from GENRES_FILMS FG " +
                    "    left join GENRES G on FG.GENRE_ID = G.GENRE_ID " +
                    "where FG.FILM_ID IN (" + StringUtils.join(filmsId, ',') + ") " +
                    "order by genreId ";
            List<Map<String, Object>> genres = jdbcTemplate.queryForList(sqlQueryGetAllGenres);
            genres.forEach(t -> mapFilms.get(Long.parseLong(t.get("filmId").toString()))
                    .getGenres()
                    .add(Genre.valueOf(t.get("genreName").toString())
                    ));
        }

        return films;
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
}
