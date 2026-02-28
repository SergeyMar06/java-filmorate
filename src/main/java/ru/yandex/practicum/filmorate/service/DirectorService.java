package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public List<Director> findAll() {
        return directorRepository.findAll();
    }

    public Director findById(Integer id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Director not found"));
    }

    public Director create(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new IllegalArgumentException("Имя режиссёра не может быть пустым");
        }

        return directorRepository.save(director);
    }

    public Director update(Director newDirector) {
        findById(newDirector.getId());

        if (newDirector.getName() == null || newDirector.getName().isBlank()) {
            throw new IllegalArgumentException("Имя режиссёра не может быть пустым");
        }
        return directorRepository.update(newDirector);
    }

    public void delete(Integer id) {
        directorRepository.delete(id);
    }
}
