package com.hackathon.medreminder.user.service;

import com.hackathon.medreminder.user.CustomUserDetails;
import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.exception.UserAlreadyExistsByEmail;
import com.hackathon.medreminder.user.exception.UserAlreadyExistsByUsername;
import com.hackathon.medreminder.user.exception.UserNotFoundById;
import com.hackathon.medreminder.user.exception.UserNotFoundByUsername;
import com.hackathon.medreminder.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User createUser(User user) {
        validateUserIsNew(user.getEmail(), user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundByUsername(username));
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundById(id));
    }


    public void validateUserIsNew(String email, String username){
        if (userRepository.existsByEmail(email)){
            throw new UserAlreadyExistsByEmail(email);
        }
        if (userRepository.existsByUsername(username)){
            throw new UserAlreadyExistsByUsername(username);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserEntityByUsername(username);
        return new CustomUserDetails(user);
    }
}