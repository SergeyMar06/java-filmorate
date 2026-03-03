package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getInt("eventId"));
        event.setUserId(resultSet.getInt("userId"));
        event.setEntityId(resultSet.getInt("entityId"));
        event.setEventType(resultSet.getString("eventType"));
        event.setOperation(resultSet.getString("operation"));
        event.setTimestamp(resultSet.getTimestamp("timestamp").toInstant());
        return event;
    }
}
