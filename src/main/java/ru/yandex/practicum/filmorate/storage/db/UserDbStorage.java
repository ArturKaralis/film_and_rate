package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository("userStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT U.USER_ID, " +
                "U.EMAIL, " +
                "U.LOGIN, " +
                "U.USER_NAME, " +
                "U.BIRTHDAY, " +
                "FROM USERS AS U";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs, rowNum));
    }

    @Override
    public User getById(long id) throws ObjectNotFoundException {
        String sqlQuery = "SELECT U.USER_ID, " +
                "U.EMAIL, " +
                "U.LOGIN, " +
                "U.USER_NAME, " +
                "U.BIRTHDAY, " +
                "FROM USERS AS U " +
                "WHERE U.USER_ID = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs, rowNum), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id=" + id + " не существует", id));
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return getById(user.getId());
    }

    @Override
    public User delete(long id) {
        String sqlQuery = "DELETE FROM USERS WHERE ISER_ID = ?;";
        jdbcTemplate.update(sqlQuery, id);
        return null;
    }

    @Override
    public void makeFriends(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO FRIENDSHIPS (USER_ID, FRIEND_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFriends(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIPS WHERE USER_ID = ? AND FRIEND_ID = ?;";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getUserFriendsById(Long userId) {
        String sqlQuery = "select U.USE_ID," +
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

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("USER_ID");
        String email = rs.getString("EMAIL");
        String login = rs.getString("LOGIN");
        String name = rs.getString("USER_NAME");
        LocalDate birthday = rs.getDate("BIRTHDAY").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}
