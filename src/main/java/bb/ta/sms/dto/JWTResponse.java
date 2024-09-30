package bb.ta.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWTResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private Date expiryDate;
}
