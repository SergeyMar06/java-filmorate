package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;

@Configuration
public class UserRepositoryTestConfig {
    @Bean
    public UserRepository userRepository(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        return new UserRepository(jdbcTemplate, userRowMapper);
    }

    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }
}