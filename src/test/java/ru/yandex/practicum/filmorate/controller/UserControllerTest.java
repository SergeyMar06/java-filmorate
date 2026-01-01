package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setName("Сергей");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        // создаём пользователя (create() void)
        controller.create(user);

        // достаём созданного пользователя через findAll()
        Collection<User> allUsers = controller.findAll();
        assertThat(allUsers).hasSize(1);

        User created = allUsers.iterator().next();
        assertThat(created.getId()).isPositive();
        assertThat(created.getName()).isEqualTo("Сергей");
        assertThat(created.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void shouldSetLoginAsNameIfNameIsNull() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setBirthday(LocalDate.of(1990, 5, 20));
        user.setName(null);

        controller.create(user);

        Collection<User> allUsers = controller.findAll();
        User created = allUsers.iterator().next();

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
