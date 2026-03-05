package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody Review review) {
        log.info("Полученный отзыв: " + review.toString());
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") int id) {
        reviewService.removeReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable("id") int id) {
        return reviewService.getReviewByReviewId(id);
    }

    @GetMapping
    public Collection<Review> getPopularReviews(@RequestParam(value = "filmId", required = false) Integer filmId,
                                                @RequestParam(value = "count", defaultValue = "10") int count) {
        if (filmId == null) {
            return reviewService.getAllReviews(count);
        } else {
            return reviewService.getReviewsByFilmId(filmId, count);

        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int reviewId, @PathVariable("userId") int userId) {
        reviewService.addReviewLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") int reviewId, @PathVariable("userId") int userId) {
        reviewService.addReviewDislike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int reviewId, @PathVariable("userId") int userId) {
        reviewService.removeReviewLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") int reviewId, @PathVariable("userId") int userId) {
        reviewService.removeReviewDislike(reviewId, userId);
    }
}