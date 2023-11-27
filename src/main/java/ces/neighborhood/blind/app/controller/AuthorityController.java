package ces.neighborhood.blind.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthorityController {

    @RequestMapping("/login")
    public String login(Model model) {
        return "login";
    }

}
