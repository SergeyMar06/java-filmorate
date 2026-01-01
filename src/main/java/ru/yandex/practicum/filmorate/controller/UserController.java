package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public void create(@RequestBody User user) {
        userService.create(user);
    }

    @PutMapping
    public void update(@RequestBody User newUser) {
        userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<Long> getFriendToUser(@PathVariable Long id) {
        return userService.getFriendsToUser(id);
    }

    @PutMapping("/{id}/friends/common/{otherId}")
    public Set<Long> getFriendsCommonOtherFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.getFriendsCommonOtherFriend(id, friendId);
    }
}
