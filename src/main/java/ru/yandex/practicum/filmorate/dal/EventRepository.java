package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Repository
public class EventRepository extends BaseRepository<Event> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM events WHERE userId = ?";
    private static final String INSERT_QUERY = "INSERT INTO events (userId, eventType, operation, entityId, timestamp) " +
            "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public List<Event> findAll(int userId) {
        return findMany(FIND_ALL_QUERY, userId);
    }

    public Event save(Event event) {
        log.info("Event in repository =" + event);
        Integer eventId = insert(
                INSERT_QUERY,
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId()

        );
        event.setEventId(eventId);
        return event;
    }
}
