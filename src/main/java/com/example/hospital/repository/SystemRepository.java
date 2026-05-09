package com.example.hospital.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SystemRepository {

    private final JdbcTemplate jdbcTemplate;

    public SystemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isDatabaseReachable() {
        Integer value = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return value != null && value == 1;
    }
}
