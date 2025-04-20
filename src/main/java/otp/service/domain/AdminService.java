package otp.service.domain;

import otp.model.OtpConfigDto;
import otp.model.User;
import otp.repo.ConfigRepo;
import otp.repo.UserRepo;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepo userRepo;
    private final ConfigRepo configRepo;

    public AdminService(UserRepo userRepo, ConfigRepo configRepo) {
        this.userRepo = userRepo;
        this.configRepo = configRepo;
    }

    public List<User> getAllNonAdmins() {
        return userRepo.findAll().stream()
                .filter(user -> !"ADMIN".equals(user.getRole()))
                .toList();
    }

    public void deleteUser(int userId) {
        userRepo.deleteById(userId);
    }

    public Optional<OtpConfigDto> getConfig() {
        return configRepo.getConfig();
    }

    public boolean updateConfig(OtpConfigDto config) {
        return configRepo.updateConfig(config);
    }
}




