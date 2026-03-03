package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {
    private DirectorRepository directorRepository;

    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public List<Director> findAll() {
        return directorRepository.findAll();
    }

    public Director findById(Integer id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Director not found"));
    }

    public Director create(Director director) {
        return directorRepository.save(director);
    }

    public Director update(Director newDirector) {
        return directorRepository.update(newDirector);
    }

    public void delete(Integer id) {
        directorRepository.delete(id);
    }
}
