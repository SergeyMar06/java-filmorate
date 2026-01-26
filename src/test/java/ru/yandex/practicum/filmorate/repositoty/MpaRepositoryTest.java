package ru.yandex.practicum.filmorate.repositoty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.config.MpaRepositoryTestConfig;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaRepositoryTestConfig.class)
class MpaRepositoryTest {

    @Autowired
    private MpaRepository mpaRepository;

    @Test
    void shouldFindAllMpa() {
        assertThat(mpaRepository.findAll())
                .isNotEmpty();
    }

    @Test
    void shouldFindMpaById() {
        Optional<Mpa> mpaOptional = mpaRepository.findById(1);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa.getId()).isEqualTo(1)
                );
    }
}
