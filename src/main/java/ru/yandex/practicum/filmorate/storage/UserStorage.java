package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends Storage<User> {
    List<User> makeFriends(Long userId, Long friendId);

    List<User> removeFriends(Long userId, Long friendId);

    List<User> getUserFriendsById(Long userId);

    void isUserExisted(long id);
}