package ces.neighborhood.blind.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BoardController {

    @RequestMapping("/board")
    public String board() {
        return "/board";
    }

}
