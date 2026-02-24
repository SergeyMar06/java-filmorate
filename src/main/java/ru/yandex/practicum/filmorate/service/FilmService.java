package ru.yandex.practicum.filmorate.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

@Service
public class FilmService {

    private FilmRepository filmRepository;
    private MpaService mpaService;
    private GenreRepository genreRepository;

    public FilmService(FilmRepository filmRepository, MpaService mpaService, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.mpaService = mpaService;
        this.genreRepository = genreRepository;
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
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new BadRequestException("Описание слишком длинное");
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
        filmRepository.likeFilm(filmId, userId);
    }

    public void removeLikeTheMovie(Integer filmId, Integer userId) {
        filmRepository.removeLike(filmId, userId);
    }

    public List<Film> getFilmWithTheMostLikes(Integer count) {
        return filmRepository.findMostLikedFilms(count);
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
}
