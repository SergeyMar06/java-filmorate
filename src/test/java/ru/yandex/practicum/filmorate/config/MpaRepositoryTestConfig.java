package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;

@Configuration
public class MpaRepositoryTestConfig {
    @Bean
    public MpaRepository mpaRepository(JdbcTemplate jdbcTemplate, MpaRowMapper mpaRowMapper) {
        return new MpaRepository(jdbcTemplate, mpaRowMapper);
    }

    @Bean
    public MpaRowMapper mpaRowMapper() {
        return new MpaRowMapper();
    }
}
