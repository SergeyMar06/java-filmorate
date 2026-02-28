package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommenderService;

import java.sql.SQLException;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class RecommenderController {
    private final RecommenderService recommenderService;

    public RecommenderController(RecommenderService recommenderService) {
        this.recommenderService = recommenderService;
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendedFilms(@PathVariable Integer id) throws SQLException {
        return recommenderService.getRecommendedMoviesForUser(id); //возвращаем список фильмов
    }
}