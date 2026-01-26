package ru.yandex.practicum.filmorate.repositoty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.config.FilmRepositoryTestConfig;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(FilmRepositoryTestConfig.class)
class FilmRepositoryTest {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSaveAndFindFilmById() {
        Film film = createFilm();

        Film savedFilm = filmRepository.save(film);

        Optional<Film> filmOptional = filmRepository.findById(savedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f.getName()).isEqualTo("Test film")
                );
    }

    @Test
    void shouldFindAllFilms() {
        filmRepository.save(createFilm());

        List<Film> films = filmRepository.findAll();

        assertThat(films).hasSize(1);
    }

    @Test
    void shouldUpdateFilm() {
        Film film = filmRepository.save(createFilm());

        film.setName("Updated name");
        filmRepository.update(film);

        Film updatedFilm = filmRepository.findById(film.getId()).orElseThrow();
        assertThat(updatedFilm.getName()).isEqualTo("Updated name");
    }

    @Test
    void shouldReturnMostLikedFilms() {
        // создаём пользователя
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@mail.ru", "login", "name", LocalDate.of(2000, 1, 1)
        );

        Film film = filmRepository.save(createFilm());

        filmRepository.likeFilm(film.getId(), 1);

        List<Film> popularFilms = filmRepository.findMostLikedFilms(10);

        assertThat(popularFilms)
                .isNotEmpty()
                .first()
                .extracting(Film::getId)
                .isEqualTo(film.getId());
    }

    private Film createFilm() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);

        return film;
    }
}