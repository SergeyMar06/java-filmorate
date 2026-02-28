package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";

    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";

    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";

    private static final String FIND_BY_FILM_ID = """
            SELECT d.id, d.name
            FROM directors d
            JOIN film_director fd ON d.id = fd.director_id
            WHERE fd.film_id = ?
            ORDER BY d.id""";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> findById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Director save(Director director) {
        Integer id = insert(
                INSERT_QUERY,
                director.getName()
        );

        director.setId(id);

        return director;
    }

    public Director update(Director newDirector) {
        update(
                UPDATE_QUERY,
                newDirector.getName(),
                newDirector.getId()
        );

        return newDirector;
    }

    public void delete(Integer id) {
        delete(DELETE_QUERY, id);
    }

    public void saveFilmDirectors(Integer filmId, Collection<Director> directors) {

        String sql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";

        List<Object[]> batchArgs = directors
                .stream()
                .map(director -> new Object[]{filmId, director.getId()})
                .toList();

        jdbc.batchUpdate(sql, batchArgs);
    }

    public void deleteFilmDirectors(Integer filmId) {
        jdbc.update("DELETE FROM film_director WHERE film_id = ?", filmId);
    }

    public List<Director> getDirectorsByFilmId(Integer filmId) {

        List<Director> directors = jdbc.query(FIND_BY_FILM_ID, (rs, rowNum) -> {
            Director d = new Director();
            d.setId(rs.getInt("id"));
            d.setName(rs.getString("name"));
            return d;
        }, filmId);

        return directors;
    }
}