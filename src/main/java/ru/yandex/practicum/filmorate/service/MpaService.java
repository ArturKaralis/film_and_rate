package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> findAll() {
        return mpaStorage.getAll();
    }

    public Mpa getMpaById(Long mpaId) {
        Mpa mpa = mpaStorage.getById(mpaId);
        return mpa;
    }
}