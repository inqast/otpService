package otp.service.domain;

import otp.model.User;
import otp.repo.UserRepo;
import otp.security.Jwt;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final Jwt jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, Jwt jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(String username, String rawPassword, String role) {
        Optional<User> existingUser = userRepo.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Already exists");
        }

        if ("ADMIN".equalsIgnoreCase(role) && isAdminExists()) {
            throw new RuntimeException("Admin owerflow");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(0, username, hashedPassword, role.toUpperCase());

        return userRepo.save(newUser);
    }

    public String login(String username, String password) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Incorrect password");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }

    public boolean isAdminExists() {
        return userRepo.findAll().stream()
                .anyMatch(user -> "ADMIN".equalsIgnoreCase(user.getRole()));
    }
}
