package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
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
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
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

    private static final String FIND_ALL_FILMS_BY_DIRECTOR =
            "SELECT f.* " +
                    "FROM films f " +
                    "JOIN film_director fd ON f.id = fd.film_id " +
                    "LEFT JOIN likes l ON l.film_id = f.id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.user_id) DESC";

    private static final String FIND_ALL_FILMS_BY_YEARS =
            "SELECT f.* " +
                    "FROM films f " +
                    "JOIN film_director fd ON f.id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.release_date";
    private static final String GET_COMMON_FILMS =
            "SELECT f.* " +
                    "FROM films AS f " +
                    "JOIN likes AS l1 ON f.id = l1.film_id " +
                    "JOIN likes AS l2 ON f.id = l2.film_id " +
                    "LEFT JOIN likes AS l ON f.id = l.film_id " +
                    "WHERE l1.user_id = ? AND l2.user_id = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.user_id) DESC;";
    private static final String FIND_POPULAR_FILMS_BY_GENRE_AND_YEAR =
            "SELECT f.* " +
                    "FROM films f " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "WHERE (:genreId IS NULL OR EXISTS (" +
                    "   SELECT 1 FROM film_genre fg WHERE fg.film_id = f.id AND fg.genre_id = :genreId" +
                    ")) " +
                    "AND (:year IS NULL OR EXTRACT(YEAR FROM f.release_date) = :year) " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT :limit";

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

            film.setDirectors(getDirectorsByFilmId(film.getId()));
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

            film.setDirectors(getDirectorsByFilmId(film.getId()));
        });

        return filmOpt;
    }

    public Film save(Film film) {
        Integer id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? java.sql.Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );

        film.setId(id);

        saveFilmDirectors(id, film.getDirectors());

        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? java.sql.Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        jdbc.update("DELETE FROM film_director WHERE film_id = ?", film.getId());

        saveFilmDirectors(film.getId(), film.getDirectors());

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

            film.setDirectors(getDirectorsByFilmId(film.getId()));
        }

        return films;
    }

    public List<Film> getCommonSortedFilms(Integer userId, Integer friendId) {
        List<Film> films = jdbc.query(GET_COMMON_FILMS, mapper, userId, friendId);

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

    public List<Film> findFilmsByDirectorSortedByLikes(Integer directorId) {
        List<Film> films = jdbc.query(FIND_ALL_FILMS_BY_DIRECTOR, mapper, directorId);

        // подгружаем связи
        films.forEach(f -> {
            f.setGenres(getGenresByFilmId(f.getId()));
            if (f.getMpa() != null) f.setMpa(getMpaById(f.getMpa().getId()));
            f.setDirectors(getDirectorsByFilmId(f.getId()));
        });

        return films;
    }

    public List<Film> findFilmsByDirectorSortedByYear(Integer directorId) {
        List<Film> films = jdbc.query(FIND_ALL_FILMS_BY_YEARS, mapper, directorId);

        films.forEach(f -> {
            f.setGenres(getGenresByFilmId(f.getId()));
            if (f.getMpa() != null) f.setMpa(getMpaById(f.getMpa().getId()));
            f.setDirectors(getDirectorsByFilmId(f.getId()));
        });

        return films;
    }

    private Set<Director> getDirectorsByFilmId(Integer filmId) {
        List<Director> directors = jdbc.query(
                "SELECT d.id, d.name FROM directors d " +
                        "JOIN film_director fd ON d.id = fd.director_id " +
                        "WHERE fd.film_id = ?",
                (rs, rowNum) -> {
                    Director d = new Director();
                    d.setId(rs.getInt("id"));
                    d.setName(rs.getString("name"));
                    return d;
                },
                filmId
        );

        return new HashSet<>(directors);
    }

    private void saveFilmDirectors(Integer filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) return;

        for (Director director : directors) {
            jdbc.update(
                    "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)",
                    filmId,
                    director.getId()
            );
        }
    }

    public List<Film> findMostPopularsByGenreAndYear(Integer count, Long genreId, Integer year) {
        List<Film> films;

        if (genreId != null && year != null) {
            String sql = "SELECT f.* " +
                    "FROM films f " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "WHERE EXISTS (SELECT 1 FROM film_genre fg WHERE fg.film_id = f.id AND fg.genre_id = ?) " +
                    "AND EXTRACT(YEAR FROM f.release_date) = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";
            films = jdbc.query(sql, mapper, genreId, year, count);
        } else if (genreId != null) {
            String sql = "SELECT f.* " +
                    "FROM films f " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "WHERE EXISTS (SELECT 1 FROM film_genre fg WHERE fg.film_id = f.id AND fg.genre_id = ?) " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";
            films = jdbc.query(sql, mapper, genreId, count);
        } else if (year != null) {
            String sql = "SELECT f.* " +
                    "FROM films f " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";
            films = jdbc.query(sql, mapper, year, count);
        } else {
            return findMostLikedFilms(count);
        }

        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            if (film.getMpa() != null) {
                film.setMpa(getMpaById(film.getMpa().getId()));
            }
        }

        return films;
    }
}
