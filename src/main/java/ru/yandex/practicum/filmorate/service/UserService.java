package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventService eventService;


    public Collection<Event> getAllEventsByUserId(int userId) {
        throwIfNotExists(userId);

        return eventService.findAll(userId);
    }

    public void removeUser(int id) {
        userRepository.removeUserById(id);
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User с id = " + id + " не найден"));
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getLogin().chars().anyMatch(Character::isWhitespace)) {
            throw new InvalidFormatException("Логин не может содержать пробелов");
        }

        return userRepository.save(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new NotFoundException("Должен быть id");
        }
        throwIfNotExists(newUser.getId());

        if (newUser.getLogin().chars().anyMatch(Character::isWhitespace)) {
            throw new InvalidFormatException("Логин не может содержать пробелов");
        }

        return userRepository.update(newUser);
    }

    public void addFriend(Integer id, Integer friendId) {
        if (id.equals(friendId)) {
            log.warn("Попытка добавить себя в друзья: userId={}", id);
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }
        throwIfNotExists(id);
        throwIfNotExists(friendId);

        userRepository.addFriend(id, friendId);
        log.info("User {} add user {} в друзья", id, friendId);
        eventService.saveEvent(id, friendId, EventType.FRIEND, Operation.ADD);
    }

    public List<User> removeFromFriends(Integer id, Integer friendId) {
        throwIfNotExists(id);
        throwIfNotExists(friendId);

        eventService.saveEvent(id, friendId, EventType.FRIEND, Operation.REMOVE);
        return userRepository.removeFromFriends(id, friendId);
    }

    public List<User> getFriendsToUser(Integer id) {
        throwIfNotExists(id);

        return userRepository.getFriendsToUser(id);
    }

    public List<User> getFriendsCommonOtherFriend(Integer id, Integer friendId) {
        return userRepository.getFriendsCommonOtherFriend(id, friendId);
    }

    public void throwIfNotExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}