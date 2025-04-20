package otp.service.domain;

import otp.model.Code;
import otp.model.CodeStatus;
import otp.model.OtpConfigDto;
import otp.repo.CodeRepo;
import otp.repo.ConfigRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CodeCleanerService {

    private final CodeRepo otpCodeRepo;
    private final ConfigRepo configRepo;

    public CodeCleanerService(CodeRepo otpCodeRepo, ConfigRepo configRepo) {
        this.otpCodeRepo = otpCodeRepo;
        this.configRepo = configRepo;
    }

    @Scheduled(fixedRate = 10000)
    public void expireOldCodes() {
        List<Code> activeCodes = otpCodeRepo.findAllActive();
        Optional<OtpConfigDto> configOpt = configRepo.getConfig();
        if (configOpt.isEmpty()) return;

        int ttlSeconds = configOpt.get().ttlSeconds();
        LocalDateTime now = LocalDateTime.now();

        for (Code code : activeCodes) {
            if (code.getCreatedAt().plusSeconds(ttlSeconds).isBefore(now)) {
                otpCodeRepo.updateStatus(code.getId(), CodeStatus.EXPIRED);
                log.info("[OTP] Code #{} EXPIRED", code.getId());
            }
        }
    }
}

