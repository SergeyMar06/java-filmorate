package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Data
@Component
@EqualsAndHashCode(of = {"id"})
public class ReviewLike {
    private int id;
    private int reviewId;
    private int userId;
    private boolean isLike;
}
