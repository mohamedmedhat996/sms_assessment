package bb.ta.sms.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtil {
    private final BCryptPasswordEncoder passwordEncoder;

    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public boolean passwordMatches(String storedPassword, String providedPassword) {
        return passwordEncoder.matches(providedPassword, storedPassword);
    }
}
