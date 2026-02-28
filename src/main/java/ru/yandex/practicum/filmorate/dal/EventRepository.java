package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

@Slf4j
@Repository
public class EventRepository extends BaseRepository<Event> {
    private static final String FIND_ALL_QUERY = """
            SELECT
                e.eventId,
                e.userId,
                e.entityId,
                e.eventType,
                e.operation,
                e.timestamp
            FROM events e
            WHERE userId = ?
            ORDER BY e.timestamp ASC
            """;

    private static final String INSERT_QUERY = "INSERT INTO events (userId, eventType, operation, entityId, timestamp) " +
            "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Event> findAllByUserId(Integer userId) {

        Collection<Event> events = findMany(FIND_ALL_QUERY, userId);
        log.debug("Получен список всех событий пользователя {}, количество: {}", userId, events.size());

        return events;
    }

    public Event save(Event event) {
        Integer eventId = insert(
                INSERT_QUERY,
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId()

        );
        event.setEventId(eventId);
        log.info("Event in repository =" + event);
        return event;
    }
}