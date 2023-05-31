package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository("userStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT USER_ID, " +
                "EMAIL, " +
                "LOGIN, " +
                "USER_NAME, " +
                "BIRTHDAY " +
                "FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    @Transactional
    public User getById(long id) {
        String sqlQuery = "SELECT USER_ID, " +
                "EMAIL, " +
                "LOGIN, " +
                "USER_NAME, " +
                "BIRTHDAY " +
                "FROM USERS " +
                "WHERE USER_ID = ?;";
        User user;
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        } catch (DataAccessException e) {
            return new User();
        }
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE USERS " +
                "SET EMAIL = ?, " +
                "LOGIN = ?, " +
                "USER_NAME = ?, " +
                "BIRTHDAY = ? " +
                "WHERE USER_ID = ?;";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getLogin());
                statement.setString(3, user.getName());
                statement.setDate(4, Date.valueOf(user.getBirthday()));
                statement.setLong(5, user.getId());
                return statement;
            });
        } catch (DataAccessException e) {
            return new User();
        }
        return user;
    }

    @Override
    public void delete(long id) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<User> makeFriends(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO FRIENDSHIPS (USER_ID, FRIEND_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return getUserFriendsById(userId);
    }

    @Override
    public List<User> removeFriends(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIPS WHERE USER_ID = ? AND FRIEND_ID = ?;";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return getUserFriendsById(userId);
    }

    @Override
    public List<User> getUserFriendsById(Long userId) {
        String sqlQuery = "SELECT U.USER_ID," +
                "       EMAIL," +
                "       LOGIN," +
                "       USER_NAME," +
                "       BIRTHDAY " +
                "from USERS U" +
                "    inner join FRIENDSHIPS F on U.USER_ID = F.FRIEND_ID " +
                "where F.USER_ID = ? " +
                "order by USER_ID";
        return jdbcTemplate.query(sqlQuery, this::makeUser, userId);
    }

    @Override
    public void isUserExisted(long id) {
        String sqlQuery = "SELECT USER_ID FROM USERS WHERE USER_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowSet.next()) {
            throw new ObjectNotFoundException("UПользователь с id = " + id + " не найден", id);
        }
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("USER_ID");
        String email = rs.getString("EMAIL");
        String login = rs.getString("LOGIN");
        String name = rs.getString("USER_NAME");
        LocalDate birthday = rs.getDate("BIRTHDAY").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}