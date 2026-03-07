package ru.yandex.practicum.filmorate.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserService userService;
    private final DirectorRepository directorRepository;
    private final MpaService mpaService;
    private final GenreRepository genreRepository;
    private final EventService eventService;

    public void removeFilm(int id) {
        filmRepository.removeFilmById(id);
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmRepository.findAll();

        for (Film film : films) {
            enrichFilmWithData(film);
        }

        return films;
    }

    public Film create(Film film) {
        if (film == null) {
            throw new NotFoundException("Фильма не существует");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new BadRequestException("Название фильма не может быть пустым");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Неверная дата релиза");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new BadRequestException("Продолжительность фильма должна быть положительной");
        }
        if (film.getMpa() == null) {
            throw new BadRequestException("MPA не может быть null");
        }
        if (mpaService.findById(film.getMpa().getId()) == null) {
            throw new NotFoundException("Mpa с id = " + film.getMpa().getId() + " нет");
        }

        film = filmRepository.save(film);

        updateGenres(film, false);
        updateDirectors(film, false);

        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null || !filmRepository.existsById(newFilm.getId())) {
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }

        filmRepository.update(newFilm);

        updateGenres(newFilm, true);
        updateDirectors(newFilm, true);

        return findById(newFilm.getId());
    }

    public Film findById(Integer id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));

        enrichFilmWithData(film);

        return film;
    }

    public void likeTheMovie(Integer filmId, Integer userId) {
        throwIfNotExists(filmId);
        userService.throwIfNotExists(userId);
        filmRepository.likeFilm(filmId, userId);
        eventService.saveEvent(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public void removeLikeTheMovie(Integer filmId, Integer userId) {
        throwIfNotExists(filmId);
        userService.throwIfNotExists(userId);

        filmRepository.removeLike(filmId, userId);

        eventService.saveEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    public List<Film> findAllFilmByDirector(Integer directorId, String sortBy) {
        if (directorRepository.findById(directorId).isEmpty()) {
            throw new NotFoundException("Режиссёр не найден");
        }

        List<Film> films = null;

        if ("likes".equals(sortBy)) {
            films = filmRepository.findFilmsByDirectorSortedByLikes(directorId);
        } else if ("year".equals(sortBy)) {
            films = filmRepository.findFilmsByDirectorSortedByYear(directorId);
        } else {
            throw new IllegalArgumentException("Unknown sortBy value: " + sortBy);
        }

        for (Film film : films) {
            enrichFilmWithData(film);
        }

        return films;
    }

    public List<Film> getCommonSortedFilms(Integer userId, Integer friendId) {
        List<Film> films = filmRepository.getCommonSortedFilms(userId, friendId);

        for (Film film : films) {
            enrichFilmWithData(film);
        }

        return films;
    }

    public List<Film> getPopular(Integer count, Long genreId, Integer year) {
        if (count == null) {
            count = 10;
        }

        if (genreId != null) {
            genreRepository.findById(genreId)
                    .orElseThrow(() -> new NotFoundException("Жанр с id = " + genreId + " не найден"));
        }

        List<Film> films = filmRepository.findMostPopularsByGenreAndYear(count, genreId, year);

        for (Film film : films) {
            enrichFilmWithData(film);
        }

        return films;
    }

    public List<Film> findByTitleOrDirector(String query, String by) {

        String[] result = by.split(",");
        List<Film> films = null;

        if (result.length == 2) {
            films = filmRepository.findByTitleAndDirector(query);
        } else if (result[0].equals("title")) {
            films = filmRepository.findByTitle(query);
        } else if (result[0].equals("director")) {
            films = filmRepository.findByDirector(query);
        } else throw new BadRequestException("Неверно указаны параметры строки запроса");

        for (Film film : films) {
            enrichFilmWithData(film);
        }

        return films;
    }

    public void throwIfNotExists(Integer userId) {
        if (!filmRepository.existsById(userId)) {
            throw new NotFoundException("Фильм с id = " + userId + " не найден");
        }
    }

    public Collection<Film> getUniqueMovies(Integer sourceUserId, Integer targetUserId) {
        Collection<Film> films = filmRepository.getUniqueMovies(sourceUserId, targetUserId);

        for (Film film : films) {
            enrichFilmWithData(film);
        }

        return films;
    }

    private void updateGenres(Film film, boolean isUpdate) {
        if (film.getGenres() != null) {
            Set<Genre> genres = Set.copyOf(film.getGenres());
            if (isUpdate) {
                genreRepository.deleteGenresFromFilm(film.getId());
            }
            genreRepository.updateFilmGenres(film.getId(), genres);
        }
    }

    private void updateDirectors(Film film, boolean isUpdate) {
        if (film.getDirectors() != null) {
            Set<Director> directors = Set.copyOf(film.getDirectors());
            if (isUpdate) {
                directorRepository.deleteFilmDirectors(film.getId());
            }
            directorRepository.saveFilmDirectors(film.getId(), directors);
        }
    }

    // меняет объект по ссылке
    private void enrichFilmWithData(Film film) {
        film.setGenres(genreRepository.getGenresByFilmId(film.getId()));

        if (film.getMpa() != null) {
            film.setMpa(mpaService.findById(film.getMpa().getId()));
        }

        film.setDirectors(directorRepository.getDirectorsByFilmId(film.getId()));
    }
}