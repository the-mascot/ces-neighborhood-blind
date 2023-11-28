package ces.neighborhood.blind.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @RequestMapping("/login")
    public String login(Model model) {
        return "/authority/login";
    }

    @RequestMapping("/join")
    public String join(Model model) {
        return "/authority/join";
    }

    @PostMapping("/auth/join")
    public String joinMember(@ModelAttribute LoginReqDto loginReqDto) {
        authorityService.joinMember(loginReqDto);
        return "redirect:/authority/login";
    }
}
