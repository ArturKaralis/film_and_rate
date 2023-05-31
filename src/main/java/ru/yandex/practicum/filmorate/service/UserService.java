package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public List<User> getUsers() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        validateUser(user);
        log.info("Пользователь '{}' с id '{}' был успешно добавлен.", user.getName(), user.getId());
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        getUserById(user.getId());
        return userStorage.update(user);
    }

    public User getUserById(long id) {
        userStorage.isUserExisted(id);
        User user = userStorage.getById(id);
        return user;
    }

    public void deleteUserById(long id) {
        User user = userStorage.getById(id);
        if (user != null) {
            userStorage.delete(id);
        } else {
            log.warn("Пользователь не найден. Передан отсутствующий id фильма");
            throw new ObjectNotFoundException("Пользователь", id);
        }
    }

    public void addFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> getListFriends(long id) {
        userStorage.getById(id);
        return userStorage.getUserFriendsById(id);
    }

    public List<User> getMutualFriends(long userId, long friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);
        List<User> userFriends = userStorage.getUserFriendsById(userId);
        List<User> friendFriends = userStorage.getUserFriendsById(friendId);
        if (userFriends.isEmpty() || friendFriends.isEmpty()) {
            return new ArrayList<>();
        }
        //List<User> friends = friendStorage.getCommonFriends(userId, friendId);
        userFriends.retainAll(friendFriends);
        return userFriends.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }
}