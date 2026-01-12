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

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.addFriend(id, friendId);
        userStorage.addFriend(friendId, id);
    }

    public void removeFromFriends(Long id, Long friendId) {
        userStorage.removeFromFriends(id, friendId);
        userStorage.removeFromFriends(friendId, id);
    }

    public Set<User> getFriendsToUser(Long id) {
        return userStorage.getFriendsToUser(id);
    }

    public Set<User> getFriendsCommonOtherFriend(Long id, Long friendId) {
        return userStorage.getFriendsCommonOtherFriend(id, friendId);
    }
}
