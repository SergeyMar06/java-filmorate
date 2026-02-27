package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;


@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    private static final String INSERT_FILM_GENRE =
            "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    private static final String DELETE_FILM_GENRES =
            "DELETE FROM film_genre WHERE film_id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public void addGenre(Film film) {
        List<Object[]> batchArgs = film.getGenres()
                .stream()
                .map(genre -> new Object[]{film.getId(), genre.getId()})
                .toList();

        jdbc.batchUpdate(INSERT_FILM_GENRE, batchArgs);
    }

    public void updateGenre(Film film) {
        delete(DELETE_FILM_GENRES, film.getId());
        addGenre(film);
    }
}

