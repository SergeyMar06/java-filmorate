package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;


@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    }

    public User removeFromFriends(Integer id, Integer friendId) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + id + " нет");
        }
        if (userRepository.findById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + friendId + " нет");
        }

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
