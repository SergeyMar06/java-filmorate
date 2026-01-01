package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов в количестве {}", films.size());
        return films.values();
    }

    @Override
    public Film findById(Long id) {
        Film film = films.get(id);

        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        return film;
    }

    @Override
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос на создание фильма");

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка создать фильм с пустым названием");
            throw new InvalidFormatException("Название не может быть пустым!");
        }

        if (film.getDescription().length() > 200) {
            log.warn("Попытка создать фильм с превышенной длиной описания");
            throw new InvalidFormatException("Превышена максимальная длина описания - 200");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка создать фильм с датой релиза до 28 декабря 1895 года");
            throw new InvalidFormatException("Дата релиза не должна быть раньше 28 декабря 1895 года!");
        }

        if (film.getDuration() < 0) {
            log.warn("Попытка создать фильм с отрицательной продолжительностью");
            throw new InvalidFormatException("Продолжительность фильма не может быть отрицательным числом!");
        }

        film.setId(getNextId());

        films.put(film.getId(), film);

        log.info("Фильм успешно создан с id {}", film.getId());

        return film;
    }

    @Override
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма с id {}", newFilm.getId());

        if (newFilm.getId() == null) {
            log.warn("Попытка обновить объект без указанного id");
            throw new InvalidFormatException("id должен быть указан!");
        }

        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.warn("Попытка создать фильм с пустым названием");
            throw new InvalidFormatException("Название не может быть пустым!");
        }

        if (newFilm.getDescription().length() > 200) {
            log.warn("Попытка создать фильм с превышенной длиной описания");
            throw new InvalidFormatException("Превышена максимальная длина описания - 200");
        }

        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка создать фильм с датой релиза до 28 декабря 1895 года");
            throw new InvalidFormatException("Дата релиза не должна быть раньше 28 декабря 1895 года!");
        }

        if (newFilm.getDuration() < 0) {
            log.warn("Попытка создать фильм с отрицательной продолжительностью");
            throw new InvalidFormatException("Продолжительность фильма не может быть отрицательным числом!");
        }

        Film oldFilm = films.get(newFilm.getId());

        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDuration() != 0) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        films.put(oldFilm.getId(), oldFilm);

        log.info("Фильм успешно обновлён");

        return oldFilm;
    }

    public void likeTheMovie(Long filmId, Long userId) {
        films.get(filmId).getUsersLikesFilm().add(userId);
    }

    public void removeLikeTheMovie(Long filmId, Long userId) {
        films.get(filmId).getUsersLikesFilm().remove(userId);
    }

    public Set<Film> getFilmWithTheMostLikes(Long count) {
        Set<Film> sortedFilms = getSortedFilmsById();

        if (count == null) {
            if (sortedFilms.size() < 10) {
                return sortedFilms;
            } else {
                return sortedFilms.stream()
                        .limit(10)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }
        } else {
            return sortedFilms.stream()
                    .limit(count.longValue())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
    }

    public Set<Film> getSortedFilmsById() {
        Set<Film> sortedFilms = new TreeSet<>(
                Comparator.comparingInt(f -> f.getUsersLikesFilm().size())
        );

        sortedFilms.addAll(films.values());

        return sortedFilms;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
