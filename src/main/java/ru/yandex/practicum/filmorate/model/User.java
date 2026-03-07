package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 User
 */
@Data
public class User {
    private Integer id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}