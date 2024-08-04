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
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.service.MemberService;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
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

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer"; // authorization type

    private static final  String ACCESS_TOKEN_HEADER_NAME = "Access-Token";

    private static final  String REFRESH_TOKEN_HEADER_NAME = "Refresh-Token";   // refresh token 의 응답 header name

    private static final String TOKEN_HEADER_TYPE = "JWT";

    private long ACCESS_TOKEN_EXPIRE_TIME;    // 30분

    private long REFRESH_TOKEN_EXPIRE_TIME;   // 7일

    private SecretKey key;

    private String issuer;

    private MemberService memberService;

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
        Claims claims = Jwts.claims().add(AUTHORITIES_KEY, mbrRole).subject(mbrId).issuer(issuer).build();
        return Jwts.builder()
                .header()
                .type(TOKEN_HEADER_TYPE)
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

    public TokenDto createTokenDTO(String accessToken, String refreshToken) {
        return TokenDto.builder()
                .authorizationType(BEARER_TYPE)
                .accessTokenHeaderName(ACCESS_TOKEN_HEADER_NAME)
                .refreshTokenHeaderName(REFRESH_TOKEN_HEADER_NAME)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) throws
            BizException {
        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORITIES_KEY) == null || StringUtils.isBlank(claims.get(AUTHORITIES_KEY).toString())) {
            throw new BizException(ErrorCode.CODE_1000);
        }
        log.debug("[JwtTokenProvider - getAuthentication] claims.getAuth = {}", claims.get(AUTHORITIES_KEY));
        log.debug("[JwtTokenProvider - getAuthentication] claims.getEmail = {}", claims.getSubject());

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        authorities.stream().forEach(o -> {
            log.debug("[JwtTokenProvider - getAuthentication] authorities = {}", o.getAuthority());
        });

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * refresh token 으로 access token 재발급
     * @param refreshToken
     * @return
     */
    public String refreshAccessToken(String refreshToken) {
        // 토큰 payload 가져오기, 만료여부 validation
        Claims claims = this.parseClaims(refreshToken);
        // 회원정보조회
        MbrInfo mbrInfo = memberService.getMbrInfo(claims.getSubject());

        if (!StringUtils.equals(refreshToken, mbrInfo.getRefreshToken())) {
            throw new BizException(ErrorCode.CODE_1121);
        }
        return createAccessToken(mbrInfo.getMbrId(), mbrInfo.getRole());
    }

    /**
     * 토큰 유효성 검사
     * @param token
     * @return 1: 토큰유효, 2: 토큰만료, 3: 유효하지 않은 토큰
     */
    public int validateToken(String token) {
        try {
            Jwts.parser().decryptWith(key).build().parseSignedClaims(token).getPayload();
            return 1;
        } catch (ExpiredJwtException e) {
            log.info("[JwtTokenProvider] 만료된 JWT 토큰입니다.");
            return 2;
        } catch (Exception e) {
            log.debug("[JwtTokenProvider] 유효하지 않은 토큰입니다.");
            return -1;
        }
    }

    /**
     * 토큰에서 payload 부분 가져오기
     * @param accessToken
     * @return
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().decryptWith(key).build().parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            throw new BizException(ErrorCode.CODE_1120);
        } catch (Exception e) {
            throw new BizException(ErrorCode.CODE_1121);
        }
    }

    /**
     * request header 에서 access token 가져오기
     * @param request
     * @return
     */
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER_NAME);
        if (StringUtils.isNotBlank(refreshToken)) {
            return refreshToken;
        }

        return null;
    }
}
