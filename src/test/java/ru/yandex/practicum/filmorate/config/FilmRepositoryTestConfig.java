package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;

@Configuration
public class FilmRepositoryTestConfig {
    @Bean
    public FilmRepository filmRepository(JdbcTemplate jdbcTemplate,
                                         FilmRowMapper filmRowMapper) {
        return new FilmRepository(jdbcTemplate, filmRowMapper);
    }

    @Bean
    public FilmRowMapper filmRowMapper() {
        return new FilmRowMapper();
    }
}