package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.PairOfUsersWithCommonInterests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommenderService {
    private final JdbcTemplate jdbc;

    public Collection<Film> getRecommendedMoviesForUser(Integer targetUserId) throws SQLException {
        // Получаем пары пользователей с общими интересами
        Collection<PairOfUsersWithCommonInterests> pairs = getPairsOfUsersWithCommonInterests(targetUserId);

        // Набор фильмов для рекомендаций
        List<Film> recommendedMovies = new ArrayList<>();

        // Проходим по каждой паре пользователей
        for (PairOfUsersWithCommonInterests pair : pairs) {
            // Определяем фильмы, которые понравились другому пользователю, но не понравились целевому пользователю
            Collection<Film> uniqueMovies = getUniqueMovies(pair.getFirstUserId(), targetUserId);
            recommendedMovies.addAll(uniqueMovies);
        }

        return recommendedMovies;
    }

    private Collection<Film> getUniqueMovies(Integer sourceUserId, Integer targetUserId) throws SQLException {
        String sql = """
                WITH source_user_likes AS (
                    SELECT film_id
                    FROM likes
                    WHERE user_id = ?
                ),
                target_user_likes AS (
                    SELECT film_id
                    FROM likes
                    WHERE user_id = ?
                )
                SELECT f.*
                FROM films f
                INNER JOIN source_user_likes sul ON f.id = sul.film_id
                LEFT JOIN target_user_likes tul ON f.id = tul.film_id
                WHERE tul.film_id IS NULL;
                """;

        RowMapper<Film> mapper = (rs, rowNum) -> createFilmFromResultSet(rs);

        return jdbc.query(sql, mapper, sourceUserId, targetUserId);
    }

    private Film createFilmFromResultSet(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        return film;
    }

    public Collection<PairOfUsersWithCommonInterests> getPairsOfUsersWithCommonInterests(Integer targetUserId)
            throws SQLException {
        String sql = """
                SELECT l1.user_id AS user1_id, l2.user_id AS user2_id, COUNT(*) AS common_likes_count
                FROM likes l1
                JOIN likes l2 ON l1.film_id = l2.film_id AND l1.user_id <> l2.user_id
                WHERE l1.user_id = ? OR l2.user_id = ?
                GROUP BY l1.user_id, l2.user_id
                HAVING COUNT(*) > 0 -- Отбираем только тех, у кого есть общие лайки
                ORDER BY common_likes_count DESC;
                """;

        RowMapper<PairOfUsersWithCommonInterests> mapper = (rs, rowNum) ->
                new PairOfUsersWithCommonInterests(
                        rs.getInt("user1_id"),
                        rs.getInt("user2_id"),
                        rs.getInt("common_likes_count")
                );

        return new ArrayList<>(jdbc.query(sql, mapper, targetUserId, targetUserId));
    }
}