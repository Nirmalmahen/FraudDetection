package com.fraud.repository;

import com.fraud.mapper.UserRowMapper;
import com.fraud.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String fetchUserQuery;

    public UserRepository() {
        try {
            this.fetchUserQuery = loadQueryFromFile("queries.sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadQueryFromFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public User getUserByAccountId(String accountId) {
        String sql = fetchUserQuery.replace("{tableName}", "users");
        return jdbcTemplate.queryForObject(sql, new Object[]{accountId}, new UserRowMapper());
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{username}, new UserRowMapper());
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userQuery = "SELECT * FROM users WHERE username = ?";
        String rolesQuery = "SELECT r.role_name FROM roles r JOIN " +
                "users ur ON r.id = ur.role_id WHERE ur.user_id = ?";

        User user = jdbcTemplate.queryForObject(userQuery, new Object[]{username}, new UserRowMapper());

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        List<String> roles = jdbcTemplate.queryForList(rolesQuery, new Object[]{user.getUserId()}, String.class);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

}
