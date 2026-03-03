package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable Long id) {
        return directorService.findById(id);
    }

    @PostMapping
    public Director create(@RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@RequestBody Director newDirector) {
        return directorService.update(newDirector);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        directorService.delete(id);
    }
}
