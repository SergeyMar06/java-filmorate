package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    public Collection<Event> getAllEventsByUserId(int userId) {
        return eventRepository.findAll(userId);
    }

    public void removeUser(int id) {
        userRepository.removeUserById(id);
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (!user.getLogin().chars().noneMatch(Character::isWhitespace)) {
            throw new InvalidFormatException("Логин не может содержать пробелов");
        }

        return userRepository.save(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == null || userRepository.findById(newUser.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (!newUser.getLogin().chars().noneMatch(Character::isWhitespace)) {
            throw new InvalidFormatException("Логин не может содержать пробелов");
        }

        return userRepository.update(newUser);
    }

    public void addFriend(Integer id, Integer friendId) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + id + " нет");
        }
        if (userRepository.findById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + friendId + " нет");
        }

        userRepository.addFriend(id, friendId);
        log.info("User {} add user {} в друзья", id, friendId);
        Event event = new Event(); // добавление в ленту
        event.setEventType(EventType.FRIEND.toString());
        event.setUserId(id); // актор добавил в друзья
        event.setEntityId(friendId);
        event.setOperation(Operation.ADD.toString());
        log.info("event внутри userService =" + event);
        eventRepository.save(event); // добавление в ленту
    }

    public Set<User> removeFromFriends(Integer id, Integer friendId) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + id + " нет");
        }
        if (userRepository.findById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + friendId + " нет");
        }
        Event event = new Event(); // добавление в ленту
        event.setEventType(EventType.FRIEND.toString());
        event.setUserId(id); // актор удалил из друзей
        event.setEntityId(friendId); // кого удалил
        event.setOperation(Operation.REMOVE.toString());
        eventRepository.save(event); // добавление в ленту
        return userRepository.removeFromFriends(id, friendId);
    }

    public Set<User> getFriendsToUser(Integer id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + id + " нет");
        }
        return userRepository.getFriendsToUser(id);
    }

    public Set<User> getFriendsCommonOtherFriend(Integer id, Integer friendId) {
        return userRepository.getFriendsCommonOtherFriend(id, friendId);
    }
}
