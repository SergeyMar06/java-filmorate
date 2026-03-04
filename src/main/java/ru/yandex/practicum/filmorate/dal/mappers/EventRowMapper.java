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

        return Event.builder()
                .eventId(resultSet.getInt("eventId"))
                .userId(resultSet.getInt("userId"))
                .entityId(resultSet.getInt("entityId"))
                .eventType(resultSet.getString("eventType"))
                .operation(resultSet.getString("operation"))
                .timestamp(resultSet.getTimestamp("timestamp").toInstant().toEpochMilli())
                .build();
    }
}