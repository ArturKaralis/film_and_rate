package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private String sqlForCommonAndGetAll = "SELECT * FROM USERS " +
            "WHERE USER_ID IN (SELECT FRIEND_ID " +
            "FROM FRIENDSHIPS " +
            "WHERE USER_ID = ?) ";

    @Override
    public void addFriend(long id, long friendId) {
        String sqlQuery = "INSERT INTO FRIENDSHIPS (USER_ID,FRIEND_ID) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIPS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        String sqlQuery = "SELECT USERS.* " +
                "FROM USERS " +
                "JOIN FRIENDSHIPS AS F1 on(USERS.USER_ID = F1.FRIEND_ID AND F1.USER_ID = ?) " +
                "JOIN FRIENDSHIPS AS F2 on (USERS.USER_ID = F2.FRIEND_ID AND F2.USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFriends, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(long id) {
        return jdbcTemplate.query(sqlForCommonAndGetAll, this::mapRowToFriends, id);
    }

    private User mapRowToFriends(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("USER_ID");
        String email = rs.getString("EMAIL");
        String login = rs.getString("LOGIN");
        String name = rs.getString("USER_NAME");
        LocalDate birthday = rs.getDate("BIRTHDAY").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}
