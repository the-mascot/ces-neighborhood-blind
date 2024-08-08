package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.service.MemberService;
import ces.neighborhood.blind.common.code.Constant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(name = Constant.BASE_API_URL + "/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping(path = "/member/info")
    public ResponseEntity getMbrInfo(@RequestParam String mbrId, HttpServletRequest request, HttpServletResponse response) {
        log.error("here");
        return ApiResponse.success(memberService.getMbrInfo(mbrId));
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity verifyIdDuplicate(@Valid @PathVariable String userId) {
        return ApiResponse.success(memberService.verifyIdDuplicate(userId));
    }
}
