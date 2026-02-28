package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RecommenderRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.PairOfUsersWithCommonInterests;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommenderService {

    private final UserService userService;
    private final FilmService filmService;
    private final RecommenderRepository recommenderRepository;

    public Collection<Film> getRecommendedMoviesForUser(Integer targetUserId) throws SQLException {
        userService.throwIfNotExists(targetUserId);

        // Получаем пары пользователей с общими интересами
        Collection<PairOfUsersWithCommonInterests> pairs =
                recommenderRepository.getPairsOfUsersWithCommonInterests(targetUserId);

        // Набор фильмов для рекомендаций
        Set<Film> recommendedMovies = new HashSet<>();

        // Проходим по каждой паре пользователей
        for (PairOfUsersWithCommonInterests pair : pairs) {
            // Определяем фильмы, которые понравились другому пользователю, но не понравились целевому пользователю
            Collection<Film> uniqueMovies = filmService.getUniqueMovies(pair.getFirstUserId(), targetUserId);

            recommendedMovies.addAll(uniqueMovies);
        }

        return recommendedMovies;
    }
}