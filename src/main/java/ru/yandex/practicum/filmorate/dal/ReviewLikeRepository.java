package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;

@Repository
public class ReviewLikeRepository extends ru.yandex.practicum.filmorate.dal.BaseRepository<ReviewLike> {
    private static final String INSERT_QUERY_ADD_LIKE_OR_DISLIKE = "INSERT INTO reviewLikes (reviewId, userId, isLike) VALUES (?, ?, ?)";
    private static final String CHECKING_CONTAINS_IDS_IN_TABLE = "SELECT COUNT(*) FROM reviewLikes WHERE" +
            " reviewId = ? AND userId = ? AND isLike = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM reviewLikes WHERE reviewId = ? AND userId = ? AND isLike = ?";

    public ReviewLikeRepository(JdbcTemplate jdbc, RowMapper<ReviewLike> mapper) {
        super(jdbc, mapper);
    }

    public boolean addLikeOrDislike(ReviewLike reviewLike) {
        Integer count = jdbc.queryForObject(CHECKING_CONTAINS_IDS_IN_TABLE, Integer.class, reviewLike.getReviewId(), reviewLike.getUserId(), reviewLike.isLike());
        if (count == 0) {
            jdbc.update(INSERT_QUERY_ADD_LIKE_OR_DISLIKE, reviewLike.getReviewId(), reviewLike.getUserId(), reviewLike.isLike());
            return true;
        } else {
            System.out.println("Строка с такими значениями уже есть в таблице");
        }
        return false;
    }

    public boolean deleteLikeOrDislike(ReviewLike reviewLike) {
        return delete(DELETE_LIKE_QUERY, reviewLike.getReviewId(), reviewLike.getUserId(), reviewLike.isLike());
    }
}