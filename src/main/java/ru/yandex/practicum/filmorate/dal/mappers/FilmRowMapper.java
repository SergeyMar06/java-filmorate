package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(resultSet.getInt("id"));
        film.setDescription(resultSet.getString("description"));
        film.setName(resultSet.getString("name"));
        film.setDuration(resultSet.getInt("duration"));

//        Timestamp releaseDate = resultSet.getTimestamp("release_date");
//        if (releaseDate != null) {
//            film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());
//        }
        java.sql.Date releaseDate = resultSet.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        }


        long mpaId = resultSet.getLong("mpa_id");
        if (!resultSet.wasNull()) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            film.setMpa(mpa);
        }

        return film;
    }
}
