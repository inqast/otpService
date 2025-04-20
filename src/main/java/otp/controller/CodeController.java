package otp.controller;

import otp.controller.request.OtpGenerateRequest;
import otp.controller.request.OtpValidationRequest;
import otp.controller.response.OtpResponse;
import otp.model.User;
import otp.repo.UserRepo;
import otp.security.Context;
import otp.service.domain.CodeService;
import otp.service.notifier.NotifierService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/otp")
public class CodeController {

    private final CodeService otpService;
    private final UserRepo userDao;
    private final Context authHelper;

    private final NotifierService notifier;

    public CodeController(
        CodeService otpService, 
        UserRepo userDao, 
        Context authHelper,
        NotifierService notifier) {
        this.otpService = otpService;
        this.userDao = userDao;
        this.authHelper = authHelper;
        this.notifier = notifier;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody OtpGenerateRequest request) {
        String username = authHelper.getCurrentUsername();
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = otpService.generateOtp(user.getId(), request.operationId());

        notifier.notify(code, request.channel(), request.destination());

        return ResponseEntity.ok("Succes");
    }

    @PostMapping("/validate")
    public ResponseEntity<OtpResponse> validate(@RequestBody OtpValidationRequest request) {
        String username = authHelper.getCurrentUsername();
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean valid = otpService.validateOtp(user.getId(), request.operationId(), request.code());
        return valid
                ? ResponseEntity.ok(new OtpResponse("Succes"))
                : ResponseEntity.status(400).body(new OtpResponse("Invalid code"));
    }
}


