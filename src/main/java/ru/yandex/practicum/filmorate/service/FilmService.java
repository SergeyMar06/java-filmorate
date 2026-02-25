package ru.yandex.practicum.filmorate.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final DirectorRepository directorRepository;
    private final MpaService mpaService;
    private final GenreRepository genreRepository;
    private final EventRepository eventRepository;

    public void removeFilm(int id) {
        filmRepository.removeFilmById(id);
    }

    public Collection<Film> findAll() {
        return filmRepository.findAll();
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

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreRepository.addGenre(film);
        }

        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null || filmRepository.findById(newFilm.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        newFilm = filmRepository.update(newFilm);

        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            genreRepository.updateGenre(newFilm);
        }

        return newFilm;
    }

    public Optional<Film> findById(Integer id) {
        return filmRepository.findById(id);
    }

    public void likeTheMovie(Integer filmId, Integer userId) {
        if (filmRepository.findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }

        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        filmRepository.likeFilm(filmId, userId);
        Event event = new Event(); // добавление в ленту
        event.setEventType(EventType.LIKE);
        event.setUserId(userId); // юзер добавил лайк фильму
        event.setEntityId(filmId);
        event.setOperation(Operation.ADD);
        eventRepository.save(event); // добавление в ленту
    }

    public void removeLikeTheMovie(Integer filmId, Integer userId) {
        if (filmRepository.findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }

        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        filmRepository.removeLike(filmId, userId);
        Event event = new Event(); // добавление в ленту
        event.setEventType(EventType.LIKE);
        event.setUserId(userId); // юзер удалил лайк фильму
        event.setEntityId(filmId);
        event.setOperation(Operation.REMOVE);
        eventRepository.save(event); // добавление в ленту
    }

    public List<Film> getFilmWithTheMostLikes(Integer count) {
        return filmRepository.findMostLikedFilms(count);
    }

    public List<Film> findAllFilmByDirector(Integer directorId, String sortBy) {
        if (directorRepository.findById(directorId).isEmpty()) {
            throw new NotFoundException("Режиссёр не найден");
        }

        if ("likes".equals(sortBy)) {
            return filmRepository.findFilmsByDirectorSortedByLikes(directorId);
        } else if ("year".equals(sortBy)) {
            return filmRepository.findFilmsByDirectorSortedByYear(directorId);
        } else {
            throw new IllegalArgumentException("Unknown sortBy value: " + sortBy);
        }
    }

    public List<Film> getCommonSortedFilms(Integer userId, Integer friendId) {
        return filmRepository.getCommonSortedFilms(userId, friendId);
    }

    public List<Film> getPopular(Integer count, Long genreId, Integer year) {
        if (count == null) {
            count = 10;
        }

        if (genreId != null) {
            genreRepository.findById(genreId)
                    .orElseThrow(() -> new NotFoundException("Жанр с id = " + genreId + " не найден"));
        }

        return filmRepository.findMostPopularsByGenreAndYear(count, genreId, year);
    }

    public List<Film> findByTitleOrDirector(String query, String by) {
        String[] result = by.split(",");

        if (result.length == 2) {
            return filmRepository.findByTitleAndDirector(query);
        } else if (result[0].equals("title")) {
            return filmRepository.findByTitle(query);
        } else if (result[0].equals("director")) {
            return filmRepository.findByDirector(query);
        } else throw new BadRequestException("Неверно указаны параметры строки запроса");
    }
}
