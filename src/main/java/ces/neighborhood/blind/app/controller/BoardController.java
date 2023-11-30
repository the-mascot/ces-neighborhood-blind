package ces.neighborhood.blind.app.controller;

import ces.neighborhood.blind.app.entity.MbrBoard;
import java.util.Map;

import org.springframework.context.annotation.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BoardController {

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @RequestMapping("/board")
    public String board(Model model) {
        return "/board/board";
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @RequestMapping("/write")
    public String write(Model model) {
        model.addAttribute("mbrBoard", new MbrBoard());
        return "/board/write";
    }

//    @PostMapping("/board/post")
//    public String post(@ModelAttribute MbrBoard mbrBoard) {
//        System.out.println("hellod");
//        return "redirect:/board";
//    }

    @ResponseBody
    @PostMapping("/board/post")
    public String post(@RequestBody Map<String, Object> param) {
        System.out.println("hellod");
        return "redirect:/board";
    }
}
