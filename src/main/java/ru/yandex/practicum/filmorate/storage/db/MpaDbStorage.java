package ru.yandex.practicum.filmorate.storage.db;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Builder
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT m.id, " +
                "m.name " +
                "FROM MPA_ratings AS m;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMPA(rs));
    }

    @Override
    public Mpa getById(Long id) throws ValidationException {
        String sqlQuery = "SELECT m.id, " +
                "m.name " +
                "FROM MPA_ratings AS m " +
                "WHERE m.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMPA(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ValidationException("Рейтинг с id=" + id + " не существует"));
    }

    private Mpa makeMPA(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
