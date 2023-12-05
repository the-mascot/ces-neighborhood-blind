package ces.neighborhood.blind.app.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.service.AuthorityService;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        String error = (String) request.getAttribute("error");
        if (!StringUtils.isEmpty(error)) {
            model.addAttribute("loginStatus", error);
        }
        model.addAttribute("loginReqDto", new LoginReqDto());
        return "/authority/login";
    }

    @RequestMapping("/join")
    public String join(Model model) {
        // th:object 사용을 위해 view controller에 model 객체를 같이 보내줘야한다.
        model.addAttribute("loginReqDto", new LoginReqDto());
        return "/authority/join";
    }

    @PostMapping("/auth/join")
    public String joinMember(@ModelAttribute LoginReqDto loginReqDto) {
        authorityService.joinMember(loginReqDto);
        return "redirect:/authority/login";
    }
}
