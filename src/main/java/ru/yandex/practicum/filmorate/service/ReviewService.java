package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.ReviewLikeRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.model.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final FilmService filmService;
    private final EventRepository eventRepository;

    public void addReview(Review review) {
        if (!validateReview(review)) {
            log.error("некорректно заполнены поля");
            throw new ValidationException("некорректно заполнены поля"); // 400 код
        } else {
            log.error(review.getUserId() + " поле UserId");
            userService.findById(review.getUserId()); // проверка на существование User с таким id
            filmService.findById(review.getFilmId()); // проверка на существование Film с таким id. если не найдет возникнет ошибка 404
            log.info("Валидация review {} прошла успешно", review);
            int revId = reviewRepository.save(review);
            Event event = new Event(); // добавление в ленту
            event.setEventType(EventType.REVIEW);
            event.setUserId(review.getUserId()); // актор
            event.setEntityId(revId); // как узнать reviewId???
            event.setOperation(Operation.ADD);
            eventRepository.save(event); // добавление в ленту
        }
    }

    public void updateReview(Review review) {
        if (!validateReview(review) && review.getReviewId() == 0) {
            log.error("некорректно заполнены поля обновляемого объекта");
            throw new ValidationException("некорректно заполнены поля");
        } else {
            reviewRepository.update(review);
            Event event = new Event(); // добавление в ленту
            event.setEventType(EventType.REVIEW);
            event.setUserId(review.getUserId()); // актор
            event.setEntityId(review.getReviewId());
            event.setOperation(Operation.UPDATE);
            eventRepository.save(event); // добавление в ленту
        }
    }

    public Review getReviewByReviewId(int reviewId) {
        return reviewRepository.getReviewById(reviewId);
    }

    public List<Review> getAllReviews(int count) {
        return reviewRepository.findAllReviews(count);
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        return reviewRepository.findAllReviewsByFilmId(filmId, count);
    }

    public void removeReview(int id) {
        reviewRepository.deleteReview(id);
        Event event = new Event(); // добавление в ленту
        event.setEventType(EventType.REVIEW);
        event.setUserId(getReviewByReviewId(id).getUserId()); // актор
        event.setEntityId(id);
        event.setOperation(Operation.REMOVE);
        eventRepository.save(event); // добавление в ленту
    }

    public void addReviewLike(int reviewId, int userId) {
        removeReviewDislike(reviewId, userId); // если есть дизлайк с такими id, удаляем его
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setLike(true);
        reviewLike.setReviewId(reviewId);
        reviewLike.setUserId(userId);

        if (reviewLikeRepository.addLikeOrDislike(reviewLike)) { // если лайк добавлен, увеличиваем значение поля useful на 1
            reviewRepository.increaseUseful(reviewId);
            Event event = new Event(); // добавление в ленту
            event.setEventType(EventType.LIKE);
            event.setUserId(userId); // актор
            event.setEntityId(reviewId);
            event.setOperation(Operation.ADD);
            eventRepository.save(event); // добавление в ленту
        }
    }

    public void addReviewDislike(int reviewId, int userId) {
        removeReviewLike(reviewId, userId); // если есть лайк с такими id-сначала удаляем лайк
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setLike(false);
        reviewLike.setReviewId(reviewId);
        reviewLike.setUserId(userId);

        if (reviewLikeRepository.addLikeOrDislike(reviewLike)) { // если лайк добавлен, увеличиваем значение поля useful на 1
            reviewRepository.decreaseUseful(reviewId);
            Event event = new Event(); // добавление в ленту
            event.setEventType(EventType.LIKE);
            event.setUserId(userId); // действующее лицо
            event.setEntityId(reviewId);
            event.setOperation(Operation.ADD);
            eventRepository.save(event); // добавление в ленту
        }
    }

    public void removeReviewLike(int reviewId, int userId) {
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setLike(true);
        reviewLike.setReviewId(reviewId);
        reviewLike.setUserId(userId);
        if (reviewLikeRepository.deleteLikeOrDislike(reviewLike)) {
            reviewRepository.decreaseUseful(reviewId); // при удалении лайка уменьшаем useful на 1
            Event event = new Event(); // добавление в ленту
            event.setEventType(EventType.LIKE);
            event.setUserId(userId);
            event.setEntityId(reviewId);
            event.setOperation(Operation.REMOVE);
            eventRepository.save(event); // добавление в ленту
        }
    }

    public void removeReviewDislike(int reviewId, int userId) {
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setLike(false);
        reviewLike.setReviewId(reviewId);
        reviewLike.setUserId(userId);
        if (reviewLikeRepository.deleteLikeOrDislike(reviewLike)) {
            reviewRepository.increaseUseful(reviewId); // при удалении дизлайка useful +1
            Event event = new Event(); // добавление в ленту
            event.setEventType(EventType.LIKE);
            event.setUserId(userId);
            event.setEntityId(reviewId);
            event.setOperation(Operation.REMOVE);
            eventRepository.save(event); // добавление в ленту
        }
    }

    public static boolean validateReview(Review review) {
        // Проверка, что название не пустое
        if (!StringUtils.hasText(review.getContent())) {
            log.error("Не заполнено или пустое поле name");
            return false;
        }
        // Проверка isPositive
        if (review.getIsPositive() == null) {
            log.error("поле isPositive не должно быть пустым");
            return false;
        }
        if (review.getUserId() == 0) {
            log.error("Поле UserId  не заполнено или =0 ");
            return false;
        }
        if (review.getFilmId() == 0) {
            log.error("Поле FilmId  не заполнено или =0 ");
            return false;
        }
        return true; // Все проверки пройдены успешно!!!!
    }

}
