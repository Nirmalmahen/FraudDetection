package com.fraud.service;

import com.fraud.model.User;
import com.fraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByAccountId(String accountId) {
        return userRepository.getUserByAccountId(accountId);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.loadUserByUsername(username);
    }
}
