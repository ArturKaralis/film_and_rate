package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private long uniqId;
    private UserService users;

    public long generateId() {
        return ++uniqId;
    }

    public List<User> getUsers() {
        return userStorage.getAll().orElseThrow();
    }

    public User createUser(User user) {
        validateUser(user);
        log.info("Пользователь '{}' с id '{}' был успешно добавлен.", user.getName(), user.getId());
        return userStorage.create(user).orElseThrow();
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        validateUser(user);
        return userStorage.update(user).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", user.getId()));
    }

    public User getUserById(long id) {
        Optional<User> user = userStorage.getById(id);
        return user.orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", id));
    }

    public User deleteUserById(long id) {
        return userStorage.delete(id);
    }

    public List<User> addFriend(long userId, long friendId) {
        userStorage.getById(userId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", userId));
        userStorage.getById(friendId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", friendId));

        return userStorage.makeFriends(userId, friendId).orElse(new ArrayList<>());
    }

    public List<User> deleteFriend(long userId, long friendId) {
        userStorage.getById(userId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", userId));
        userStorage.getById(friendId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", friendId));

        return userStorage.removeFriends(userId, friendId).orElse(new ArrayList<>());
    }

    public List<User> getListFriends(long id) {
        userStorage.getById(id).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", id));
        return userStorage.getUserFriendsById(id).orElse(new ArrayList<>());
    }

    public List<User> getMutualFriends(long userId, long friendId) {
        userStorage.getById(userId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", userId));
        userStorage.getById(friendId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с ID %s не найден", friendId));
        List<User> userFriends = userStorage.getUserFriendsById(userId).orElse(new ArrayList<>());
        List<User> friendFriends = userStorage.getUserFriendsById(friendId).orElse(new ArrayList<>());
        if (userFriends.isEmpty() || friendFriends.isEmpty()) {
            return new ArrayList<>();
        }
        userFriends.retainAll(friendFriends);
        return userFriends.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }
}
