package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

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
        String sqlQuery = "SELECT * " +
                        "FROM USERS " +
                        "WHERE USER_ID IN (SELECT FRIEND_ID " +
                        "                   FROM FRIENDSHIPS " +
                        "                   WHERE USER_ID = ?) " +
                        "AND USER_ID IN (SELECT FRIEND_ID " +
                        "                   FROM FRIENDSHIPS " +
                        "                   WHERE USER_ID = ?)";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        List<User> commonFriends = new ArrayList<>();
        while (rs.next()) {
            commonFriends.add(new User(rs.getInt("USER_ID"),
                    rs.getString("USER_NAME"),
                    rs.getString("LOGIN"),
                    rs.getString("EMAIL"),
                    Objects.requireNonNull(rs.getDate("BIRTHDAY")).toLocalDate()));
        }
        return commonFriends.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<User> getAllFriends(long id) {
        String sqlQuery = "SELECT * " +
                "FROM USERS " +
                "WHERE USER_ID IN " +
                "(SELECT FRIEND_ID " +
                "FROM FRIENDSHIPS " +
                "WHERE USER_ID = ?)";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, id);
        List<User> friends = new ArrayList<>();
        while (rs.next()) {
            friends.add(new User(rs.getInt("USER_ID"),
                    rs.getString("USER_NAME"),
                    rs.getString("LOGIN"),
                    rs.getString("EMAIL"),
                    Objects.requireNonNull(rs.getDate("BIRTHDAY")).toLocalDate()));
        }

        return friends;
    }
}
