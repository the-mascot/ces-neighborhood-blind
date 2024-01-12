package ces.neighborhood.blind.app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import ces.neighborhood.blind.app.dto.AuthorizationCodeReqDto;
import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.service.AuthorityService;
import ces.neighborhood.blind.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    /**
     * 로그인 페이지
     */
    @RequestMapping("/login")
    public String login(@RequestParam(required = false) boolean error , Model model) {
        if (error) {
            model.addAttribute("loginStatus", ErrorCode.CODE_1005.getMessage());
        }
        model.addAttribute("loginReqDto", new LoginReqDto());
        return "/authority/login";
    }

    /**
     * 회원가입 페이지
     */
    @RequestMapping("/join")
    public String join(Model model) {
        // th:object 사용을 위해 view controller에 model 객체를 같이 보내줘야한다.
        model.addAttribute("loginReqDto", new LoginReqDto());
        return "/authority/join";
    }

    /**
     * 회원가입
     */
    @PostMapping("/auth/join")
    public String joinMember(@ModelAttribute LoginReqDto loginReqDto) {
        authorityService.joinMember(loginReqDto);
        return "redirect:/authority/login";
    }

    /**
     * OAuth2 커스텀 로그인
     */
    @GetMapping("/oauth/login")
    public String oAuth2Login(HttpServletRequest request, HttpServletResponse response) {
        //String redirectUri = authorityService.authorize();
        StringKeyGenerator stringKeyGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder());
        String encodedRedirectUri = UriUtils.encode("http://localhost:8010/oauth/redirect", StandardCharsets.UTF_8);
        String encodedState = UriUtils.encode(stringKeyGenerator.generateKey(), StandardCharsets.UTF_8);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("https://nid.naver.com/oauth2.0/authorize");
        uriBuilder.queryParam("response_type", "code");
        uriBuilder.queryParam("client_id", "UpEJjnGzwwLj_hk4mbB6");
        uriBuilder.queryParam("redirect_uri", "http://localhost:8010/oauth/redirect");
        uriBuilder.queryParam("state", encodedState);

        return "redirect:" + uriBuilder.toUriString();
    }

    @GetMapping("/error")
    public String errorPage() {
        return "/error/error";
    }

}
