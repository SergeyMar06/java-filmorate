package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;


@Service
public class UserService {

    private UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id);
    }

    public void create(User user) {
        userStorage.create(user);
    }

    public void update(User newUser) {
        userStorage.update(newUser);
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.addFriend(id, friendId);
    }

    public void removeFromFriends(Long id, Long friendId) {
        userStorage.removeFromFriends(id, friendId);
    }

    public Set<Long> getFriendsToUser(Long id) {
        return userStorage.getFriendsToUser(id);
    }

    public Set<Long> getFriendsCommonOtherFriend(Long id, Long friendId) {
        return userStorage.getFriendsCommonOtherFriend(id, friendId);
    }
}
