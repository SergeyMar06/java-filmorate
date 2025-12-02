package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private HashMap<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей в количестве {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя");

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Попытка создать пользователя с пустым email или email без символа @");
            throw new InvalidFormatException("Email не может быть пустым и должен содержать символ @!");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Попытка создать пользователя с пустым login или содержащим пробелы");
            throw new InvalidFormatException("Login не может быть пустым и содержать пробелы!");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка создать пользователя с датой рождения в будущем");
            throw new InvalidFormatException("Дата рождения не может быть в будущем!");
        }

        user.setId(getNextId());

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);

        log.warn("Пользователь успешно создан с id {}", user.getId());

        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя с id {}", newUser.getId());

        if (newUser.getId() == null) {
            log.warn("Попытка обновить объект без указанного id");
            throw new InvalidFormatException("id должен быть указан!");
        }

        if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
            log.warn("Попытка создать пользователя с пустым email или email без символа @");
            throw new InvalidFormatException("Email не может быть пустым и должен содержать символ @!");
        }

        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.warn("Попытка создать пользователя с пустым login или содержащим пробелы");
            throw new InvalidFormatException("Login не может быть пустым и содержать пробелы!");
        }

        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка создать пользователя с датой рождения в будущем");
            throw new InvalidFormatException("Дата рождения не может быть в будущем!");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }

            users.put(oldUser.getId(), oldUser);

            log.warn("Пользователь успешно обновлён");

            return oldUser;
        }

        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден!");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
