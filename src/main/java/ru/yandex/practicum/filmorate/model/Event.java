package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"eventId"})
public class Event {
    private Long timestamp;
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer eventId;
    private Integer entityId;
}