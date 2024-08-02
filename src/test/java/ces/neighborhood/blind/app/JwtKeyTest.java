package ces.neighborhood.blind.app;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

public class JwtKeyTest {

    @Test
    public void JwtKeyTest() {
        String encodeSecretKey = Encoders.BASE64.encode("northstar/ces/neighborhood/blind/key/for/react".getBytes(
                StandardCharsets.UTF_8));
        System.out.println("encodeSecretKey : " + encodeSecretKey);
        System.out.println("encodeSecretKey length : " + encodeSecretKey.length());
        byte[] decodeSecretKey = Decoders.BASE64.decode(encodeSecretKey);
        System.out.println("decodeSecretKey : " + decodeSecretKey);
        System.out.println("decodeSecretKey length : " + decodeSecretKey.length);
        System.out.println("decodeSecretKey bit : " + decodeSecretKey.length * 8);
        SecretKey key = Keys.hmacShaKeyFor(decodeSecretKey);
        Date now = new Date();
        Claims claims = Jwts.claims().subject("dmstn1812@naver.com").issuer("neighborhood").build();
        String token = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 60000))
                .signWith(key)
                .compact();
        System.out.println(token);
    }

}
