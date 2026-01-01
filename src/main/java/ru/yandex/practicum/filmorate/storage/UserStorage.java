package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    User findById(Long id);

    void addFriend(Long id, Long friendId);

    void removeFromFriends(Long id, Long friendId);

    Set<Long> getFriendsToUser(Long id);
    
    Set<Long> getFriendsCommonOtherFriend(Long id, Long friendId);
}
