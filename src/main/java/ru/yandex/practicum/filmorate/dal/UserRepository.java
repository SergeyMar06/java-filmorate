package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";

    private static final String INSERT_FRIEND =
            "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";

    private static final String DELETE_FRIEND =
            "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

    private static final String FIND_FRIENDS =
            "SELECT u.* FROM users u " +
                    "JOIN friendship f ON u.id = f.friend_id " +
                    "WHERE f.user_id = ?";

    private static final String FIND_COMMON_FRIENDS =
            "SELECT u.* FROM users u " +
                    "JOIN friendship f1 ON u.id = f1.friend_id " +
                    "JOIN friendship f2 ON u.id = f2.friend_id " +
                    "WHERE f1.user_id = ? AND f2.user_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public void removeUserById(int id) {
        boolean deleted = delete(DELETE_USER_QUERY, id);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findById(Integer userId) {
        Optional<User> optUser = findOne(FIND_BY_ID_QUERY, userId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User с таким id = " + userId + " не найден");
        }
        return optUser;
    }

    public User save(User user) {
        Integer id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()

        );

        user.setId(id);

        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }

    public void addFriend(Integer id, Integer friendId) {
        jdbc.update(INSERT_FRIEND, id, friendId);
    }

    public List<User> removeFromFriends(Integer id, Integer friendId) {
        jdbc.update(DELETE_FRIEND, id, friendId);

        return getFriendsToUser(id);
    }

    public List<User> getFriendsToUser(Integer id) {
        List<User> friends = jdbc.query(
                FIND_FRIENDS,
                mapper,
                id
        );

        return List.copyOf(friends);
    }

    public List<User> getFriendsCommonOtherFriend(Integer id, Integer friendId) {
        List<User> commonFriends = jdbc.query(
                FIND_COMMON_FRIENDS,
                mapper,
                id,
                friendId
        );

        return List.copyOf(commonFriends);
    }
}
