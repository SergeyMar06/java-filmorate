package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Film.
 */
@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Название должно быть не пустым")
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private List<Long> usersLikesFilm = new ArrayList<>();
    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
    private List<Director> directors = new ArrayList<>();
}
