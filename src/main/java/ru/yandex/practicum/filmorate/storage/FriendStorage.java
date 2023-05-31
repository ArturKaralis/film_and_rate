package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    List<User> getCommonFriends(long id, long otherId);

    List<List<User>> getAllFriends(long id);
}
