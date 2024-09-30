package bb.ta.sms.serviceImpl;

import bb.ta.sms.dto.JWTResponse;
import bb.ta.sms.dto.UserRequest;
import bb.ta.sms.model.User;
import bb.ta.sms.repository.UserRepository;
import bb.ta.sms.service.UserService;
import bb.ta.sms.util.JwtUtil;
import bb.ta.sms.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;

    public JWTResponse authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordUtil.passwordMatches(user.get().getPassword(), password)) {
            HashMap<String, Object> claims = getJwtClaims(user.get());
            String accessToken = jwtUtil.generateToken(user.get().getUsername(), claims);
            String refreshToken = jwtUtil.generateRefreshToken(user.get().getUsername(), claims);
            return JWTResponse.builder()
                    .expiryDate(jwtUtil.getTokenExpireDate())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(user.get().getEmail())
                    .build();
        }
        return null;
    }

    @Override
    public JWTResponse refreshAuthentication(String refreshToken) {
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        if (jwtUtil.validateRefreshToken(refreshToken, username)) {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                HashMap<String, Object> claims = getJwtClaims(user.get());
                String newAccessToken = jwtUtil.generateToken(username, claims);
                return JWTResponse.builder()
                        .expiryDate(jwtUtil.getTokenExpireDate())
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .email(user.get().getEmail())
                        .build();
            }
            return null;
        }
        return null;
    }

    @Override
    public User addUser(UserRequest request) {
        User newUser = User.builder().username(request.getUsername()).password(passwordUtil.hashPassword(request.getPassword()))
                .email(request.getEmail()).role(request.getRole()).build();
        return userRepository.save(newUser);
    }

    private HashMap<String, Object> getJwtClaims(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("userName", user.getUsername());
        return claims;
    }
}
