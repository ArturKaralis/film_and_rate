package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class MpaService {

    private final MpaStorage mpaDbStorage;

    public List<Mpa> findAll() {
        return mpaDbStorage.getAll();
    }

    public Mpa getMpaById(Long mpaId) {
        Mpa mpa = mpaDbStorage.getById(mpaId);
        if (mpa == null) {
            throw new ObjectNotFoundException("Категория рейтинга", mpaId);
        }
        return mpa;
    }
}