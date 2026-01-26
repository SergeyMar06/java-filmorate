package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    private static final String INSERT_QUERY =
            "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                    "WHERE id = ?";

    private static final String FIND_GENRES_BY_FILM =
            "SELECT genre_id FROM film_genre WHERE film_id = ? ORDER BY genre_id";

    private static final String INSERT_LIKE =
            "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

    private static final String DELETE_LIKE =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String FIND_POPULAR_FILMS =
            "SELECT f.* " +
                    "FROM films f " +
                    "LEFT JOIN likes fl ON f.id = fl.film_id " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(fl.user_id) DESC " +
                    "LIMIT ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);

        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            if (film.getMpa() != null) {
                film.setMpa(getMpaById(film.getMpa().getId()));
            }
        }

        return films;
    }

    public Optional<Film> findById(long filmId) {
        Optional<Film> filmOpt = findOne(FIND_BY_ID_QUERY, filmId);

        filmOpt.ifPresent(film -> {
            film.setGenres(getGenresByFilmId(film.getId()));
            if (film.getMpa() != null) {
                film.setMpa(getMpaById(film.getMpa().getId()));
            }
        });

        return filmOpt;
    }

    public Film save(Film film) {
        Integer id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );

        film.setId(id);

        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        return film;
    }

    public void likeFilm(Integer filmId, Integer userId) {
        jdbc.update(INSERT_LIKE, filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        jdbc.update(DELETE_LIKE, filmId, userId);
    }

    public List<Film> findMostLikedFilms(Integer count) {
        List<Film> films = jdbc.query(FIND_POPULAR_FILMS, mapper, count);

        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            if (film.getMpa() != null) {
                film.setMpa(getMpaById(film.getMpa().getId()));
            }
        }

        return films;
    }


    private Set<Genre> getGenresByFilmId(Integer filmId) {
        List<Genre> genres = jdbc.query(
                "SELECT g.id, g.name FROM genres g " +
                        "JOIN film_genre fg ON g.id = fg.genre_id " +
                        "WHERE fg.film_id = ? ORDER BY g.id",
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                },
                filmId
        );

        return new HashSet<>(genres);
    }

    private Mpa getMpaById(Long mpaId) {
        return jdbc.queryForObject(
                "SELECT id, name FROM mpa WHERE id = ?",
                (rs, rowNum) -> {
                    Mpa m = new Mpa();
                    m.setId(rs.getLong("id"));
                    m.setName(rs.getString("name"));
                    return m;
                },
                mpaId
        );
    }

}
