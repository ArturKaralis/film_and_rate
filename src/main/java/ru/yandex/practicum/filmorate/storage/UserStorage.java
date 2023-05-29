package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends Storage<User> {
    void makeFriends(Long userId, Long friendId);

    void removeFriends(Long userId, Long friendId);

    List<User> getUserFriendsById(Long userId) throws ObjectNotFoundException;
}
