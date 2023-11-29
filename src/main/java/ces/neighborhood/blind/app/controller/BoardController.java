package ces.neighborhood.blind.app.controller;

import org.springframework.context.annotation.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BoardController {

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @RequestMapping("/board")
    public String board() {
        return "/board/board";
    }



}
