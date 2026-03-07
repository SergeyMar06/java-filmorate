package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void saveEvent(Integer userId, Integer entityId, EventType eventType, Operation operation) {

        Event event = Event.builder()
                .eventType(eventType.toString())
                .userId(userId)
                .entityId(entityId)
                .operation(operation.toString())
                .build();

        eventRepository.save(event);
    }

    public Collection<Event> findAll(Integer userId) {
        return eventRepository.findAllByUserId(userId);
    }
}