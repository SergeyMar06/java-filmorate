package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReviewRepository extends ru.yandex.practicum.filmorate.dal.BaseRepository<Review> {
    private static final String FIND_REVIEW_BY_ID_QUERY = "SELECT * FROM reviews WHERE reviewId = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String FIND_ALL_QUERY_BY_FILM_ID = "SELECT * FROM reviews WHERE filmId = ? ORDER BY useful DESC FETCH FIRST ? ROWS ONLY";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE reviewId = ?";
    private static final String UPDATE_REVIEW_BY_ID_QUERY = "UPDATE reviews SET content = ?, isPositive = ? WHERE reviewId = ?";
    private static final String INSERT_QUERY = "INSERT INTO reviews(content, filmId, userId, isPositive) VALUES (?, ?, ?, ?)"; // useful по умолчанию 0
    private static final String UPDATE_USEFUL_QUERY = "UPDATE reviews SET useful = useful + 1 WHERE reviewId = ?";
    private static final String DECREASE_USEFUL_QUERY = "UPDATE reviews SET useful = useful - 1 WHERE reviewId = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);

    }

    public Review getReviewById(int reviewId) {
        Optional<Review> review = findOne(FIND_REVIEW_BY_ID_QUERY, reviewId);
        if (review.isEmpty()) {
            throw new NotFoundException("Отзыв с таким id не найден");
        } else {
            return review.get();
        }
    }

    public List<Review> findAllReviews(int count) {
        return findMany(FIND_ALL_QUERY, count);
    }

    public List<Review> findAllReviewsByFilmId(int filmId, int count) {
        return findMany(FIND_ALL_QUERY_BY_FILM_ID, filmId, count);
    }

    public void update(Review review) {
        log.info("{} объект передается в репозиторий", review);

        update(
                UPDATE_REVIEW_BY_ID_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
    }

    public Integer save(Review review) {
        return insert(
                INSERT_QUERY,
                review.getContent(),
                review.getFilmId(),
                review.getUserId(),
                review.getIsPositive()
        );
    }

    public void deleteReview(int reviewId) {
        boolean deleted = delete(DELETE_REVIEW_QUERY, reviewId);
        if (!deleted) {
            throw new InternalServerException("Не удалось удалить отзыв"); // 500
        }
    }

    public void increaseUseful(int reviewId) {
        jdbc.update(UPDATE_USEFUL_QUERY, reviewId);
    }

    public void decreaseUseful(int reviewId) {
        jdbc.update(DECREASE_USEFUL_QUERY, reviewId);
    }
}