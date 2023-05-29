package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
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
        return userStorage.getAll();
    }

    public User createUser(User user) {
        validateUser(user);
        log.info("Пользователь '{}' с id '{}' был успешно добавлен.", user.getName(), user.getId());
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        validateUser(user);
        return userStorage.update(user);
    }

    public User getUserById(long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new ObjectNotFoundException("Пользователь", id);
        }
        return user;
    }

    public User deleteUserById(long id) {
        return userStorage.delete(id);
    }

    public void addFriend(long id, long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new NullPointerException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        userStorage.makeFriends(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new ValidationException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        List<User> friendsUser = userStorage.getUserFriendsById(id);
        List<User> friendsFriend = userStorage.getUserFriendsById(friendId);
        if (friendsUser.contains(friendId)) {
            friendsUser.remove(friendId);
            friendsFriend.remove(id);
        }
    }

    public List<User> getListFriends(long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new ValidationException("Пользователь с Id " + id + " не найден");
        }
        return userStorage.getUserFriendsById(id);
    }

    public List<User> getMutualFriends(long id, long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if ((friendId <= 0) || (id <= 0)) {
            throw new ValidationException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        List<User> friendsUser = userStorage.getUserFriendsById(id);
        List<User> friendsFriend = userStorage.getUserFriendsById(id);
        if (friendsUser.size() == 0 || friendsFriend.size() == 0 || friendsUser.get(Math.toIntExact(getUserById(id).getId())) !=
                friendsFriend.get(Math.toIntExact(getUserById(id).getId()))) {
            throw new IncorrectPathDataException("Опа! Это совсем не друзья");
        }
        return friendsUser.stream()
                .filter(friendsFriend::contains)
                .collect(Collectors.toList());
    }
}
