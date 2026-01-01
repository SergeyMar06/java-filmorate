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

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Попытка создать фильм с превышенной длиной описания");
            throw new InvalidFormatException("Превышена максимальная длина описания - 200");
        }

        if (film.getReleaseDate() == null) {
            throw new InvalidFormatException("Дата релиза не может быть пустой!");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка создать фильм с датой релиза до 28 декабря 1895 года");
            throw new InvalidFormatException("Дата релиза не должна быть раньше 28 декабря 1895 года!");
        }

        if (film.getDuration() != null && film.getDuration() < 0) {
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

        if (newFilm.getDescription() != null && newFilm.getDescription().length() > 200) {
            log.warn("Попытка создать фильм с превышенной длиной описания");
            throw new InvalidFormatException("Превышена максимальная длина описания - 200");
        }

        if (newFilm.getReleaseDate() == null) {
            throw new InvalidFormatException("Дата релиза не может быть пустой!");
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

        if (oldFilm == null) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDuration() != null && newFilm.getDuration() >= 0) {
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
        Film film = films.get(filmId);

        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (film.getUsersLikesFilm() == null) {
            film.setUsersLikesFilm(new HashSet<>());
        }

        film.getUsersLikesFilm().add(userId);
    }

    public void removeLikeTheMovie(Long filmId, Long userId) {
        Film film = films.get(filmId);

        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (film.getUsersLikesFilm() != null) {
            film.getUsersLikesFilm().remove(userId);
        }
    }

    public Set<Film> getFilmWithTheMostLikes(Long count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(
                        f2.getUsersLikesFilm() != null ? f2.getUsersLikesFilm().size() : 0,
                        f1.getUsersLikesFilm() != null ? f1.getUsersLikesFilm().size() : 0
                ))
                .limit(count != null ? count : 10)
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
