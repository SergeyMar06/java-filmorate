package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setName("Сергей");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        User created = controller.create(user);

        assertThat(created.getId()).isPositive();
        assertThat(controller.findAll()).contains(created);
        assertThat(created.getName()).isEqualTo("Сергей");
    }

    @Test
    void shouldSetLoginAsNameIfNameIsNull() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setBirthday(LocalDate.of(1990, 5, 20));
        user.setName(null);

        User created = controller.create(user);

        assertThat(created.getName()).isEqualTo("user123");
    }

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("user123");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        assertThatThrownBy(() -> controller.create(user))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("Email не может быть пустым и должен содержать символ @");
    }

    @Test
    void shouldThrowWhenLoginIsEmptyOrContainsSpace() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user 123"); // содержит пробел
        user.setBirthday(LocalDate.of(1990, 5, 20));

        assertThatThrownBy(() -> controller.create(user))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("Login не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setBirthday(LocalDate.now().plusDays(1)); // будущая дата

        assertThatThrownBy(() -> controller.create(user))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("Дата рождения не может быть в будущем");
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentUser() {
        User user = new User();
        user.setId(999L);
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        assertThatThrownBy(() -> controller.update(user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id = 999 не найден");
    }
}
