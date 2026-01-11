package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Set;

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
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void likeTheMovie(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.likeTheMovie(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLikeTheMovie(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLikeTheMovie(filmId, userId);
    }

    @GetMapping("/popular")
    public Set<Film> getFilmWithTheMostLikes(@RequestParam("count") Long count) {
        return filmService.getFilmWithTheMostLikes(count);
    }
}
