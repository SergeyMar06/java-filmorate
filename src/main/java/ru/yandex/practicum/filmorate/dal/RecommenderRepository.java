package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.PairOfUsersWithCommonInterests;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

@Repository
@RequiredArgsConstructor
public class RecommenderRepository {

    private final JdbcTemplate jdbc;

    public Collection<PairOfUsersWithCommonInterests> getPairsOfUsersWithCommonInterests(Integer targetUserId)
            throws SQLException {
        // Отбираем только тех, у кого есть общие лайки
        String sql = """
                SELECT l1.user_id AS user1_id, l2.user_id AS user2_id, COUNT(*) AS common_likes_count
                FROM likes l1
                JOIN likes l2 ON l1.film_id = l2.film_id AND l1.user_id <> l2.user_id
                WHERE l1.user_id = ? OR l2.user_id = ?
                GROUP BY l1.user_id, l2.user_id
                HAVING COUNT(*) > 0
                ORDER BY common_likes_count DESC;
                """;

        RowMapper<PairOfUsersWithCommonInterests> mapper = (rs, rowNum) ->
                new PairOfUsersWithCommonInterests(
                        rs.getInt("user1_id"),
                        rs.getInt("user2_id"),
                        rs.getInt("common_likes_count")
                );

        return new HashSet<>(jdbc.query(sql, mapper, targetUserId, targetUserId));
    }
}