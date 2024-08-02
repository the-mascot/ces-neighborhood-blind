package ces.neighborhood.blind.app.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import ces.neighborhood.blind.app.dto.TokenDto;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String HEADER_TYPE = "JWT";
    private long ACCESS_TOKEN_EXPIRE_TIME;    // 30분
    private long REFRESH_TOKEN_EXPIRE_TIME;   // 7일
    private SecretKey key;
    private String issuer;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-token-expire-time}") long accessTime,
                            @Value("${jwt.refresh-token-expire-time}") long refreshTime,
                            @Value("${jwt.issuer}") String issuer
                            ) throws Exception {
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.issuer = issuer;
    }

    protected String createToken(String mbrId, String mbrRole, long tokenValid) {
        Date now = new Date();
        Claims claims = Jwts.claims().add("", mbrRole).subject(mbrId).issuer(issuer).build();
        return Jwts.builder()
                .header()
                .type(HEADER_TYPE)
                .and()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + tokenValid))
                .signWith(key)
                .compact();
    }

    public String createAccessToken(String mbrId, String mbrRole) {
        return this.createToken(mbrId, mbrRole, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String mbrId, String mbrRole) {
        return this.createToken(mbrId, mbrRole, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String getMemberEmailByToken(String token) {
        // 토큰의 claim의 sub 키에 이메일 값이 들어있다.
        return this.parseClaims(token).getSubject();
    }

    public TokenDto createTokenDTO(String accessToken, String refreshToken) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(BEARER_TYPE)
                .build();
    }

    public Authentication getAuthentication(String accessToken) throws
            BizException {
        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORITIES_KEY) == null || !StringUtils.isNotBlank(claims.get(AUTHORITIES_KEY).toString())) {
            throw new BizException(ErrorCode.CODE_1000);
        }
        log.debug("claims.getAuth = {}", claims.get(AUTHORITIES_KEY));
        log.debug("claims.getEmail = {}", claims.getSubject());

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        authorities.stream().forEach(o -> {
            log.debug("getAuthentication -> authorities = {}", o.getAuthority());
        });

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public int validateToken(String token) {
        try {
            Jwts.parser().decryptWith(key).build().parseSignedClaims(token).getPayload().getExpiration();
            return 1;
        } catch (ExpiredJwtException e) {
            log.info("[JwtTokenProvider] 만료된 JWT 토큰입니다. token : {}", token);
            return 2;
        } catch (Exception e) {
            log.debug("잘못된 토큰입니다.");
            return -1;
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().decryptWith(key).build().parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
