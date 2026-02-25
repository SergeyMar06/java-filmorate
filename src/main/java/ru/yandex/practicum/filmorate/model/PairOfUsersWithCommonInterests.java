package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PairOfUsersWithCommonInterests {
    private Integer firstUserId;
    private Integer secondUserId;
    private Integer commonLikesCount;

}