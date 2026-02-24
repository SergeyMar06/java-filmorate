package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@RequestMapping("/films")
public class FilmController {

    public FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @GetMapping("/{id}")
    public Optional<Film> findById(@PathVariable Integer id) {
        return filmService.findById(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void likeTheMovie(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.likeTheMovie(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLikeTheMovie(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.removeLikeTheMovie(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
            @RequestParam(value = "genreId", required = false) Long genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonSortedFilms(@RequestParam("userId") Integer userId,
            @RequestParam("friendId") Integer friendId) {
        return filmService.getCommonSortedFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findAllFilmByDirector(@PathVariable Integer directorId,
                                            @RequestParam("sortBy") String sortBy) {
        return filmService.findAllFilmByDirector(directorId, sortBy);
    }
}
