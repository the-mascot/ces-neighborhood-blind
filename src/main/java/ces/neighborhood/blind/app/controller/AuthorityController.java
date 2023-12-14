package ces.neighborhood.blind.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.service.AuthorityService;
import ces.neighborhood.blind.common.exception.ErrorCode;
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
}
