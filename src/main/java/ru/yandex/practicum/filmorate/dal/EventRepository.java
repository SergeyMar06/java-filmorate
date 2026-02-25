package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;


@Repository
public class EventRepository extends BaseRepository<Event> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM events WHERE userId = ?";
    private static final String INSERT_QUERY = "INSERT INTO events (userId, eventType, operation, entityId)" +
            "VALUES (?, ?, ?, ?)";

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public List<Event> findAll(int userId) {
        return findMany(FIND_ALL_QUERY, userId);
    }

    public void save(Event event) {
        Integer id = insert(
                INSERT_QUERY,
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()

        );
    }
}