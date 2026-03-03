package security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component  // Spring manages this as a Bean — can be injected anywhere
public class JwtUtil {

    // Secret key for signing tokens — in production, load from environment variable!
    private static final String SECRET = "nebulastock-secret";
    private static final long EXPIRATION_TIME = 864_000_000; // 24 hours in milliseconds

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Generate a JWT token for a given username and role
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)  // Who this token is for
                .claim("role", role) //Extra data inside the token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }
    // Extract user from token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    //Check if token is expired or invalid
    public boolean isTokenValid(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
