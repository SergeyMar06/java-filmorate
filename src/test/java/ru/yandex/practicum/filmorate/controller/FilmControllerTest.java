package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Фильм о космосе");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);

        Film created = controller.create(film);

        assertThat(created.getId()).isPositive();
        assertThat(controller.findAll()).contains(created);
    }

    @Test
    void shouldThrowWhenNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        assertThatThrownBy(() -> controller.create(film))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("Название не может быть пустым!");
    }

    @Test
    void shouldThrowWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("x".repeat(201)); // > 200 символов
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);

        assertThatThrownBy(() -> controller.create(film))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("Превышена максимальная длина описания - 200");
    }

    @Test
    void shouldThrowWhenReleaseDateBeforeMin() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(90);

        assertThatThrownBy(() -> controller.create(film))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("Дата релиза не должна быть раньше 28 декабря 1895 года!");
    }

    @Test
    void shouldThrowWhenDurationNegative() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-50);

        assertThatThrownBy(() -> controller.create(film))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("Продолжительность фильма не может быть отрицательным числом!");
    }
}
