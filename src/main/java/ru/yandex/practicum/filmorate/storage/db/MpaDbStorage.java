package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
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
    public Mpa getById(long id) {
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

    @Override
    public void isMpaExisted(long id) {
        String sqlQuery = "SELECT MPA_NAME FROM MPA_RATINGS WHERE MPA_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowSet.next()) {
            throw new ObjectNotFoundException("Категория  с id: " + id + " не найден", id);
        }
    }

    private Mpa makeMPA(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getLong("MPA_ID"),
                rs.getString("MPA_NAME"));
    }
}