package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        log.info("Запрошен пользователь с id '{}'", id);
        return storageUsers.get(id);
    }

    @Override
    public User create(User user) {
            user.setId(generateId());
            storageUsers.put(user.getId(), user);
            log.info("Пользователь '{}' с id '{}' был успешно добавлен.", user.getName(), user.getId());
            return user;
    }

    @Override
    public User update(User user) {
        storageUsers.put(user.getId(), user);
        log.info("Пользователь '{}' с id '{}' был успешно обновлён.", user.getName(), user.getId());
        return user;
    }

    @Override
    public User delete(long id) {
         log.info("Запрос на удаление пользователя с id '{}'", id);
         return storageUsers.remove(id);
    }
}
