package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";

    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";

    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";

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
}