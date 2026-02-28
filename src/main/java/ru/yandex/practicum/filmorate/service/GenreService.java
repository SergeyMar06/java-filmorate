package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;


    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Genre findById(long id) {
        return genreRepository.findById(id).orElseThrow(() -> new NotFoundException("Жанр не найден"));

    }

    public Optional<Genre> findByIdOptional(long id) {
        return genreRepository.findById(id);
    }
}
