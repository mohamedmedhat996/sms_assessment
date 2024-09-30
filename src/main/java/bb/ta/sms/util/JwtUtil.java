package bb.ta.sms.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String accessTokenSecret;

    @Value("${jwt.token.validity}")
    private Long tokenValidity;

    @Value("${jwt.refresh.token.validity}")
    private Long refreshTokenValidity;

    public String generateToken(String username, Map<String, Object> claims) {
        return generateToken(username, claims, accessTokenSecret, tokenValidity);
    }

    public String generateRefreshToken(String username, Map<String, Object> claims) {
        return generateToken(username, claims, accessTokenSecret, refreshTokenValidity);
    }

    public String generateToken(String username, Map<String, Object> claims, String secret, Long tokenValidity) {
        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS384.getJcaName()))
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = getUsernameFromToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public Boolean validateRefreshToken(String token, String username) {
        final String extractedUsername = getUsernameFromToken(token);
        return extractedUsername.equals(username);
    }

    public Date getTokenExpireDate() {
        return new Date(System.currentTimeMillis() + tokenValidity);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Object getClaimFromToken(String token, String key) {
        return getClaimsFromToken(token).get(key);
    }

    public String getUsernameFromToken(String token) {
        return (String) getClaimFromToken(token, "userName");
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getClaimsFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        final Claims claims = getAllClaimsFromToken(token);
        return claims;
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(accessTokenSecret.getBytes()).build().parseClaimsJws(token).getBody();
    }
}
