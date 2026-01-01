package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Set;

@Service
public class FilmService {
    private FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public void create(Film film) {
        filmStorage.create(film);
    }

    public void update(Film newFilm) {
        filmStorage.update(newFilm);
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public void likeTheMovie(Long filmId, Long userId) {
        filmStorage.likeTheMovie(filmId, userId);
    }

    public void removeLikeTheMovie(Long filmId, Long userId) {
        filmStorage.removeLikeTheMovie(filmId, userId);
    }

    public Set<Film> getFilmWithTheMostLikes(Long count) {
        return filmStorage.getFilmWithTheMostLikes(count);
    }
}
