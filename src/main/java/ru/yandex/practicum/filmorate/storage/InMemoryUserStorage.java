package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей в количестве {}", users.size());
        return users.values();
    }

    public User findById(Long id) {
        User user = users.get(id);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return user;
    }

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

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
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

        if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка создать пользователя с датой рождения в будущем");
            throw new InvalidFormatException("Дата рождения не может быть в будущем!");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (oldUser == null) {
                throw new NotFoundException("Фильм с id = " + oldUser.getId() + " не найден");
            }

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

            users.put(oldUser.getId(), oldUser);

            log.warn("Пользователь успешно обновлён");

            return oldUser;
        }

        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден!");
    }

    public void addFriend(Long id, Long friendId) {
        User user = users.get(id);
        User friend = users.get(friendId);

        if (user == null) {
            throw new IllegalArgumentException("Пользователь с id = " + id + " не найден!");
        }
        if (friend == null) {
            throw new IllegalArgumentException("Пользователь с id = " + friendId + " не найден!");
        }

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void removeFromFriends(Long id, Long friendId) {
        User user = users.get(id);
        User friend = users.get(friendId);

        if (user == null) {
            throw new IllegalArgumentException("Пользователь с id = " + id + " не найден!");
        }
        if (friend == null) {
            throw new IllegalArgumentException("Пользователь с id = " + friendId + " не найден!");
        }

        if (user.getFriends() != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id);
        }
    }

    public Set<Long> getFriendsToUser(Long id) {
        User user = users.get(id);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        if (user.getFriends() == null) {
            return new HashSet<>();
        }

        return users.get(id).getFriends();
    }

    public Set<Long> getFriendsCommonOtherFriend(Long id, Long friendId) {
        User user1 = users.get(id);
        User user2 = users.get(friendId);

        if (user1 == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (user2 == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        Set<Long> friendsUser1 = user1.getFriends() != null ? new HashSet<>(user1.getFriends()) : new HashSet<>();
        Set<Long> friendsUser2 = user2.getFriends() != null ? user2.getFriends() : new HashSet<>();

        friendsUser1.retainAll(friendsUser2);

        return friendsUser1;
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