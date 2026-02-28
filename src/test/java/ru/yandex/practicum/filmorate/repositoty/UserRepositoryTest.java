package ru.yandex.practicum.filmorate.repositoty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.config.UserRepositoryTestConfig;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserRepositoryTestConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSaveAndFindUserById() {
        User savedUser = userRepository.save(createUser());

        Optional<User> userOptional = userRepository.findById(savedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getEmail()).isEqualTo("test@mail.ru")
                );
    }

    @Test
    void shouldFindAllUsers() {
        userRepository.save(createUser());

        assertThat(userRepository.findAll())
                .hasSize(1);
    }

    @Test
    void shouldUpdateUser() {
        User user = userRepository.save(createUser());

        user.setName("Updated");
        userRepository.update(user);

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated");
    }

    @Test
    void shouldAddAndRemoveFriend() {
        User user1 = userRepository.save(createUser());
        User user2 = userRepository.save(createSecondUser());

        userRepository.addFriend(user1.getId(), user2.getId());

        List<User> friends = userRepository.getFriendsToUser(user1.getId());
        assertThat(friends)
                .extracting(User::getId)
                .contains(user2.getId());

        userRepository.removeFromFriends(user1.getId(), user2.getId());

        friends = userRepository.getFriendsToUser(user1.getId());
        assertThat(friends)
                .extracting(User::getId)
                .doesNotContain(user2.getId());
    }

    @Test
    void shouldReturnCommonFriends() {
        User user1 = userRepository.save(createUser());
        User user2 = userRepository.save(createSecondUser());
        User common = userRepository.save(createThirdUser());

        userRepository.addFriend(user1.getId(), common.getId());
        userRepository.addFriend(user2.getId(), common.getId());

        List<User> commonFriends =
                userRepository.getFriendsCommonOtherFriend(user1.getId(), user2.getId());

        assertThat(commonFriends)
                .extracting(User::getId)
                .contains(common.getId());
    }

    private User createUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login1");
        user.setName("Test");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        return user;
    }

    private User createSecondUser() {
        User user = new User();
        user.setEmail("test2@mail.ru");
        user.setLogin("login2");
        user.setName("Test2");
        user.setBirthday(LocalDate.of(1998, 1, 1));
        return user;
    }

    private User createThirdUser() {
        User user = new User();
        user.setEmail("test3@mail.ru");
        user.setLogin("login3");
        user.setName("Test3");
        user.setBirthday(LocalDate.of(1997, 1, 1));
        return user;
    }
}