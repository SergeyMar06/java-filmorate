package ru.yandex.practicum.filmorate.dal;

import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    private static final String INSERT_QUERY =
            "INSERT INTO films (name, description, release_date, duration, mpa_id)" + "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" + "WHERE id = ?";

    private static final String FIND_GENRES_BY_FILM =
            "SELECT genre_id FROM film_genre WHERE film_id = ? ORDER BY " + "genre_id";

    private static final String MERGE_LIKE = "MERGE INTO likes (film_id, user_id) VALUES (?, ?)";

    private static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String FIND_POPULAR_FILMS =
            "SELECT f.* " + "FROM films f " + "LEFT JOIN likes fl ON f.id = fl.film_id " + "GROUP BY f.id "
                    + "ORDER BY COUNT(fl.user_id) DESC " + "LIMIT ?";

    private static final String FIND_ALL_FILMS_BY_DIRECTOR = """
            SELECT f.*
            FROM films f
            JOIN film_director fd ON f.id = fd.film_id
            LEFT JOIN likes l ON l.film_id = f.id
            WHERE fd.director_id = ?
            GROUP BY f.id
            ORDER BY COUNT(l.*) DESC
            """;


    private static final String FIND_ALL_FILMS_BY_YEARS =
            "SELECT f.* " + "FROM films f " + "JOIN film_director fd ON f.id = fd.film_id "
                    + "WHERE fd.director_id = ? " + "ORDER BY f.release_date";

    private static final String GET_COMMON_FILMS =
            "SELECT f.* " + "FROM films AS f " + "JOIN likes AS l1 ON f.id = l1.film_id "
                    + "JOIN likes AS l2 ON f.id = l2.film_id " + "LEFT JOIN likes AS l ON f.id = l.film_id "
                    + "WHERE l1.user_id = ? AND l2.user_id = ? " + "GROUP BY f.id " + "ORDER BY COUNT(l.user_id) DESC;";

    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String FIND_FILM_BY_TEMPLATE = """
            SELECT f.*
            FROM films f
            LEFT JOIN film_director AS fd ON f.id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.id
            LEFT JOIN likes l ON f.id = l.film_id
            %s
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            """;

    private static final String EXISTS_BY_ID = "SELECT EXISTS(SELECT 1 FROM films WHERE id = ?)";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public void removeFilmById(int id) {
        delete(DELETE_FILM_QUERY, id);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findById(Integer filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    public Film save(Film film) {
        Integer id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null);

        film.setId(id);

        return film;
    }

    public void update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
    }

    public void likeFilm(Integer filmId, Integer userId) {
        jdbc.update(MERGE_LIKE, filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        jdbc.update(DELETE_LIKE, filmId, userId);
    }

    public List<Film> findMostLikedFilms(Integer count) {
        return jdbc.query(FIND_POPULAR_FILMS, mapper, count);
    }

    public List<Film> getCommonSortedFilms(Integer userId, Integer friendId) {
        return jdbc.query(GET_COMMON_FILMS, mapper, userId, friendId);
    }

    public List<Film> findFilmsByDirectorSortedByLikes(Integer directorId) {
        return jdbc.query(FIND_ALL_FILMS_BY_DIRECTOR, mapper, directorId);
    }

    public List<Film> findFilmsByDirectorSortedByYear(Integer directorId) {
        return jdbc.query(FIND_ALL_FILMS_BY_YEARS, mapper, directorId);
    }

    public List<Film> findMostPopularsByGenreAndYear(Integer count, Long genreId, Integer year) {
        StringBuilder sql = new StringBuilder("""
            SELECT f.*
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            """);

        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (genreId != null) {
            conditions.add("EXISTS (SELECT 1 FROM film_genre fg WHERE fg.film_id = f.id AND fg.genre_id = ?)");
            params.add(genreId);
        }

        if (year != null) {
            conditions.add("EXTRACT(YEAR FROM f.release_date) = ?");
            params.add(year);
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append("""
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """);

        params.add(count);

        return jdbc.query(sql.toString(), mapper, params.toArray());
    }

    public List<Film> findByTitle(String title) {
        title = "%" + title + "%";
        String sql = String.format(FIND_FILM_BY_TEMPLATE, "WHERE f.name ILIKE ?");
        return jdbc.query(sql, mapper, title);
    }

    public List<Film> findByDirector(String director) {
        director = "%" + director + "%";
        String sql = String.format(FIND_FILM_BY_TEMPLATE, "WHERE d.name ILIKE ?");
        return jdbc.query(sql, mapper, director);
    }

    public List<Film> findByTitleAndDirector(String query) {
        query = "%" + query + "%";
        String sql = String.format(FIND_FILM_BY_TEMPLATE, "WHERE d.name ILIKE ? OR f.name ILIKE ?");

        return jdbc.query(sql, mapper, query, query);
    }

    public Collection<Film> getUniqueMovies(Integer sourceUserId, Integer targetUserId) {
        String sql = """
                WITH source_user_likes AS (
                    SELECT film_id
                    FROM likes
                    WHERE user_id = ?
                ),
                target_user_likes AS (
                    SELECT film_id
                    FROM likes
                    WHERE user_id = ?
                )
                SELECT f.*
                FROM films f
                INNER JOIN source_user_likes sul ON f.id = sul.film_id
                LEFT JOIN target_user_likes tul ON f.id = tul.film_id
                WHERE tul.film_id IS NULL;
                """;

        return jdbc.query(sql, mapper, sourceUserId, targetUserId);
    }

    public boolean existsById(Integer id) {
        return jdbc.queryForObject(EXISTS_BY_ID, Boolean.class, id);
    }
}