package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewLikeMapper implements RowMapper<ReviewLike> {
    @Override
    public ReviewLike mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setId(resultSet.getInt("id"));
        reviewLike.setUserId(resultSet.getInt("userId"));
        reviewLike.setReviewId(resultSet.getInt("reviewId"));
        reviewLike.setLike(resultSet.getBoolean("isLike"));
        return reviewLike;
    }
}
