package ru.yandex.practicum.filmorate.storage.db;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
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
        String sqlQuery = "SELECT MPA_ID, " +
                "MPA_NAME " +
                "FROM MPA_RATINGS";
        return jdbcTemplate.query(sqlQuery, this::makeMPA);
    }

    @Override
    public Mpa getById(Long id) throws ValidationException {
        String sqlQuery = "SELECT MPA_ID, " +
                "MPA_NAME " +
                "FROM MPA_RATINGS " +
                "WHERE MPA_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeMPA, id);
        } catch (DataAccessException e) {
            throw new ObjectNotFoundException("Mpa не найдено", id);
        }
    }

    private Mpa makeMPA(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.valueOf(rs.getString("MPA_NAME"));
    }
}
