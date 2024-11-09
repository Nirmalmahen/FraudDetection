package com.fraud.repository;

import com.fraud.exception.UserRepositoryException;
import com.fraud.mapper.UserRowMapper;
import com.fraud.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String fetchUserQuery;

    public UserRepository() {
        try {
            this.fetchUserQuery = loadQueryFromFile("queries.sql");
            logger.info("Successfully loaded query from file.");
        } catch (IOException e) {
            logger.error("Failed to load query from file", e);
            throw new UserRepositoryException("Failed to load query from file", e);
        }
    }

    private String loadQueryFromFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
        catch (Exception e){
            logger.error("Failed to loadQueryFromFile with exception",e);
            throw new UserRepositoryException("Failed to loadQueryFromFile with exception",e);
        }
    }

    @Async
    public CompletableFuture<User> getUserByAccountId(String accountId) {
        try {
            String sql = fetchUserQuery.replace("{tableName}", "users");
            logger.debug("Executing query to fetch user by account ID: {}", accountId);
            User user = jdbcTemplate.queryForObject(sql, new Object[]{accountId}, new UserRowMapper());
            return CompletableFuture.completedFuture(user);
        } catch (Exception e) {
            logger.error("Error fetching user by account ID: {}", accountId, e);
            CompletableFuture<User> future = new CompletableFuture<>();
            future.completeExceptionally(new UserRepositoryException("Error fetching user by account ID", e));
            return future;
        }
    }

    @Async
    public CompletableFuture<User> findByUsername(String username) {
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            logger.debug("Executing query to find user by username: {}", username);
            User user = jdbcTemplate.queryForObject(sql, new Object[]{username}, new UserRowMapper());
            return CompletableFuture.completedFuture(user);
        } catch (Exception e) {
            logger.error("Error finding user by username: {}", username, e);
            CompletableFuture<User> future = new CompletableFuture<>();
            future.completeExceptionally(new UserRepositoryException("Error fetching user by username", e));
            return future;
        }
    }

    @Async
    public CompletableFuture<UserDetails> loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            String userQuery = "SELECT * FROM users WHERE username = ?";
            String rolesQuery = "SELECT r.role_name FROM roles r JOIN users ur ON r.id = ur.role_id WHERE ur.user_id = ?";

            logger.debug("Loading user by username: {}", username);
            User user = jdbcTemplate.queryForObject(userQuery, new Object[]{username}, new UserRowMapper());

            if (user == null) {
                logger.warn("User not found with username: {}", username);
                CompletableFuture<UserDetails> future = new CompletableFuture<>();
                future.completeExceptionally(new UsernameNotFoundException("User not found with username: " + username));
                return future;
            }

            List<String> roles = jdbcTemplate.queryForList(rolesQuery, new Object[]{user.getUserId()}, String.class);
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
            return CompletableFuture.completedFuture(userDetails);
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            CompletableFuture<UserDetails> future = new CompletableFuture<>();
            future.completeExceptionally(new UserRepositoryException("Error loading user by username", e));
            return future;
        }
    }
}