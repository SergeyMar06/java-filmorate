package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
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

    public void updateFilmGenres(Integer filmId, Collection<Genre> genres) {

        List<Object[]> batchArgs = genres
                .stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .toList();

        jdbc.batchUpdate(INSERT_FILM_GENRE, batchArgs);
    }

    public void deleteGenresFromFilm(Integer filmId) {
        jdbc.update(DELETE_FILM_GENRES, filmId);
    }

    public List<Genre> getGenresByFilmId(Integer filmId) {
        String sql = """
                SELECT g.id, g.name
                FROM genres g
                JOIN film_genre fg ON g.id = fg.genre_id
                WHERE fg.film_id = ?
                ORDER BY g.id""";

        List<Genre> genres = jdbc.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);

        return new ArrayList<>(genres);
    }
}