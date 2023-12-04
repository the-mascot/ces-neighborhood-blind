package ces.neighborhood.blind.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.service.AuthorityService;
import ces.neighborhood.blind.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginReqDto", new LoginReqDto());
        return "/authority/login";
    }

    @PostMapping("/auth/login")
    public String loginProcess(Model model, LoginReqDto loginReqDto) {
        try {
            authorityService.login(loginReqDto, model)
        } catch (BizException bizException) {
            model.addAttribute("errMsg", "아이디 또는 비밀번호를 잘못 입력했습니다.");
            return "/authority/login";
        }

        return ;
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
