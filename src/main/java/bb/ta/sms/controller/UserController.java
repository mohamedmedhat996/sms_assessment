package bb.ta.sms.controller;

import bb.ta.sms.dto.JWTResponse;
import bb.ta.sms.dto.LoginRequest;
import bb.ta.sms.dto.TokenRefreshRequest;
import bb.ta.sms.dto.UserRequest;
import bb.ta.sms.model.User;
import bb.ta.sms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        JWTResponse token = userService.authenticate(request.getUsername(), request.getPassword());
        if (token != null) {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAuthenticationToken(@RequestBody TokenRefreshRequest request) {
        JWTResponse refreshToken = userService.refreshAuthentication(request.getRefreshToken());
        if (refreshToken != null) {
            return ResponseEntity.ok(refreshToken);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
        }
    }

    // Just to Add User for Testing
    @PostMapping("/create")
    public ResponseEntity<?> addUser(@RequestBody UserRequest request) {
        User newUser = userService.addUser(request);
        if (newUser != null) {
            return ResponseEntity.ok(newUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to Add New User");
        }
    }
}
