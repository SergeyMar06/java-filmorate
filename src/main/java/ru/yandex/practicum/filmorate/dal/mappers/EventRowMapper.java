package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getInt("eventId"));
        event.setUserId(resultSet.getInt("userId"));
        event.setEventType(EventType.valueOf(resultSet.getString("eventType")));
        event.setOperation(Operation.valueOf(resultSet.getString("operation")));
        event.setEntityId(resultSet.getInt("entityId"));
        event.setTimestamp(resultSet.getTimestamp("timestamp").toInstant());

        return event;
    }
}
