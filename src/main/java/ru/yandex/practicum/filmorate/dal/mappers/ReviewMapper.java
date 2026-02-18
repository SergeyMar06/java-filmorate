package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setFilmId(resultSet.getInt("filmId"));
        review.setUserId(resultSet.getInt("userId"));
        review.setReviewId(resultSet.getInt("reviewId"));
        review.setContent(resultSet.getString("content"));
        review.setUseful(resultSet.getInt("useful"));
        review.setIsPositive(resultSet.getBoolean("isPositive"));
        return review;
    }
}

