package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    public List<Film> getFilmWithTheMostLikes(@RequestParam("count") Integer count) {
        return filmService.getFilmWithTheMostLikes(count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findAllFilmByDirector(@PathVariable Long directorId,
                                            @RequestParam("sortBy") String sortBy) {
        return filmService.findAllFilmByDirector(directorId, sortBy);
    }
}
