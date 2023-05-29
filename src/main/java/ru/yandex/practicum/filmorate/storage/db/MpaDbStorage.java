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
        String sqlQuery = "SELECT M.ID, " +
                "M.MPA_NAME " +
                "FROM MPA_RATINGS AS M;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMPA(rs));
    }

    @Override
    public Mpa getById(Long id) throws ValidationException {
        String sqlQuery = "SELECT M.ID, " +
                "M.MPA_NAME " +
                "FROM MPA_RATINGS AS M " +
                "WHERE M.ID = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMPA(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ValidationException("Рейтинг с id=" + id + " не существует"));
    }

    private Mpa makeMPA(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID");
        String name = rs.getString("MPA_NAME");
        return new Mpa(id, name);
    }
}
