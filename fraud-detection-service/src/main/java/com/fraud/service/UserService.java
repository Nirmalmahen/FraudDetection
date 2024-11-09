package com.fraud.service;

import com.fraud.exception.UserServiceException;
import com.fraud.model.User;
import com.fraud.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Async
    public CompletableFuture<User> getUserByAccountId(String accountId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Fetching user by account ID: {}", accountId);
                User user = userRepository.getUserByAccountId(accountId).get();
                if (user == null) {
                    logger.warn("No user found with account ID: {}", accountId);
                } else {
                    logger.info("User found with account ID: {}", accountId);
                }
                return user;
            } catch (Exception e) {
                logger.error("Error fetching user by account ID: {}", accountId, e);
                throw new UserServiceException("Failed to fetch user by account ID", e);
            }
        });
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            logger.debug("Loading user by username: {}", username);
            UserDetails userDetails = userRepository.loadUserByUsername(username).get();
            if (userDetails == null) {
                logger.warn("No user found with username: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            logger.info("User loaded by username: {}", username);
            return userDetails;
        } catch (UsernameNotFoundException e) {
            logger.error("User not found exception for username: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            throw new UserServiceException("Failed to load user by username", e);
        }
    }
}
