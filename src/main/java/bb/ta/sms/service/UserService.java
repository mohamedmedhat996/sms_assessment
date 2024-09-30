package bb.ta.sms.service;

import bb.ta.sms.dto.JWTResponse;
import bb.ta.sms.dto.UserRequest;
import bb.ta.sms.model.User;

public interface UserService {
    JWTResponse authenticate(String username, String password);
    JWTResponse refreshAuthentication(String refreshToken);
    User addUser(UserRequest request);
}
