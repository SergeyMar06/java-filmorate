package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"eventId"})
public class Event {
    private Instant timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int eventId;
    private int entityId;
}
