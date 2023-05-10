package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validator.UserValidator.validateUser;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storageUsers = new HashMap();
    private long uniqId;

    private long generateId() {
        return ++uniqId;
    }

    @Override
    public List<User> getAll() {
        log.info("Запрошен список всех пользователей.");
        return new ArrayList<>(storageUsers.values());
    }

    @Override
    public User getById(long id) {
        if (storageUsers.containsKey(id)) {
            log.info("Запрошен пользователь с id '{}'", id);
            return storageUsers.get(id);
        } else {
            validateUser(null);
            return null;
        }
    }

    @Override
    public User create(User user) {
        if (user != null) {
        user.setId(generateId());
        storageUsers.put(user.getId(), user);
        log.info("Пользователь '{}' с id '{}' был успешно добавлен.", user.getName(), user.getId());
        return user;
        }
        return null;
    }

    @Override
    public User update(User user) {
        if (storageUsers.containsKey(user.getId())) {
            storageUsers.put(user.getId(), user);
            log.info("Пользователь '{}' с id '{}' был успешно обновлён.", user.getName(), user.getId());
            return user;
        }
        return user;
    }

    @Override
    public User delete(long id) {
        if (storageUsers.get(id) != null) {
            log.info("Запрос на удаление пользователя с id '{}'", id);
            return storageUsers.remove(id);
        }
        return null;
    }
}
