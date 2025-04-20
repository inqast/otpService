package otp.controller;

import otp.model.OtpConfigDto;
import otp.model.User;
import otp.service.domain.AdminService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(adminService.getAllNonAdmins());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Succes");
    }

    @GetMapping("/config")
    public ResponseEntity<OtpConfigDto> getConfig() {
        return adminService.getConfig()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/config")
    public ResponseEntity<String> updateConfig(@RequestBody OtpConfigDto config) {
        if (adminService.updateConfig(config)) {
            return ResponseEntity.ok("Succes");
        }

        return ResponseEntity.badRequest().build();
    }
}

