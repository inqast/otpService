package otp.service.domain;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import otp.model.Code;
import otp.model.CodeStatus;
import otp.model.OtpConfigDto;
import otp.repo.CodeRepo;
import otp.repo.ConfigRepo;

@Slf4j
@Service
public class CodeService {

    private final CodeRepo codeRepo;
    private final ConfigRepo configRepo;

    public CodeService(CodeRepo codeRepo,
                          ConfigRepo configRepo) {
        this.codeRepo = codeRepo;
        this.configRepo = configRepo;
    }

    public String generateOtp(int userId, String operationId) {
        Optional<Code> existing = codeRepo.findActiveCode(userId, operationId);
        if (!existing.isEmpty()) {
            Code otp = existing.get();

            codeRepo.updateStatus(otp.getId(), CodeStatus.EXPIRED);
        }

        OtpConfigDto config = configRepo.getConfig()
                .orElseThrow(() -> new RuntimeException("config is empty"));

        String code = generateNumericCode(config.codeLength());
        codeRepo.save(userId, code, operationId);

        return code;
    }

    public boolean validateOtp(int userId, String operationId, String codeToCheck) {
        Optional<Code> existing = codeRepo.findActiveCode(userId, operationId);
        if (existing.isEmpty()) return false;

        Code otp = existing.get();

        if (isExpired(otp)) {
            codeRepo.updateStatus(otp.getId(), CodeStatus.EXPIRED);
            return false;
        }

        if (otp.getCode().equals(codeToCheck)) {
            codeRepo.updateStatus(otp.getId(), CodeStatus.USED);
            return true;
        }

        return false;
    }

    private boolean isExpired(Code otp) {
        OtpConfigDto config = configRepo.getConfig()
                .orElseThrow(() -> new RuntimeException("config is empty"));

        LocalDateTime now = LocalDateTime.now();
        return otp.getCreatedAt().plusSeconds(config.ttlSeconds()).isBefore(now);
    }

    private String generateNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}

