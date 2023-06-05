package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> findAll() {
        log.info("Получение списка MPA");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaRating(@PathVariable("id") Long mpaId) {
        log.info("Получение MPA по id: {}", mpaId);
        return mpaService.getMpaById(mpaId);
    }
}