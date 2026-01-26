package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;

@Configuration
public class GenreRepositoryTestConfig {
    @Bean
    public GenreRepository genreRepository(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        return new GenreRepository(jdbcTemplate, genreRowMapper);
    }

    @Bean
    public GenreRowMapper genreRowMapper() {
        return new GenreRowMapper();
    }
}