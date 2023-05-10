package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final Storage<User> userStorage;
    private long uniqId;

    public long generateId() {
        return ++uniqId;
    }

    public List<User> getUsers() {
        return userStorage.getAll();
    }

    public User createUser(@RequestBody @Valid User user) {
        validateUser(user);
        log.info("Пользователь '{}' с id '{}' был успешно добавлен.", user.getName(), user.getId());
        return userStorage.create(user);
    }

    public User updateUser(@RequestBody @Valid User user) {

        if (userStorage.getById(user.getId()) == null) {
            log.warn("Запрос на обновление пользователя с id '{}' отклонён. Он отсутствует в списке пользователей.",
                    user.getId());
            throw new ObjectNotFoundException("Список пользователей не содержит такого ID", user.getId());
        }
        validateUser(user);
        return userStorage.update(user);
    }

    public User getUserById(@PathVariable long id) {
        getNotFoundUserById(id);
        return userStorage.getById(id);
    }

    public User deleteUserById(@PathVariable long id) {
        getNotFoundUserById(id);
        return userStorage.delete(id);
    }

    public long getNotFoundUserById(Long id) {
        if (!getUsers().contains(id)) {
            log.warn("Пользователь не найден. Передан отсутствующий id пользователя");
            throw new ObjectNotFoundException("Пользователь", id);
        }
        return id;
    }

    public void addFriend(long id, long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new ValidationException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        Set<Long> friendsUser = user.getFriends();
        Set<Long> friendsFriend = friend.getFriends();
        friendsUser.add(friendId);
        friendsFriend.add(id);
    }

    public void deleteFriend(long id, long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new ValidationException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        Set<Long> friendsUser = user.getFriends();
        Set<Long> friendsFriend = friend.getFriends();
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
        return user.getFriends().stream()
                .map(u -> userStorage.getById(u))
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(long id, long friendId) {
        List<User> mutualFriends = new ArrayList<>();
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new javax.validation.ValidationException("Пользователь с Id " + id + " не найден или друг с Id " + friendId + " не найден");
        }
        Set<Long> friendsUser = user.getFriends();
        Set<Long> friendsFriend = friend.getFriends();
        for (Long userId : friendsUser) {
            if (friendsFriend.contains(userId)) {
                mutualFriends.add(userStorage.getById(userId));
            }
        }
        return mutualFriends;
    }
}
