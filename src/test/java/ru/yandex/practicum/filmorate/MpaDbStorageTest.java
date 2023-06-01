package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    @Order(1)
    public void testFindOne() {
        Mpa mpa = mpaStorage.getById((long) 1);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);
    }

    @Test
    @Order(2)
    public void testFindAll() {
        List<Mpa> mpaList = mpaStorage.getAll();

        assertThat(mpaList).isNotNull();
        assertThat(mpaList.size()).isEqualTo(5);
    }
}
