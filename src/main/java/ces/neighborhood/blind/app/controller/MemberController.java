package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.neighborhood.blind.app.service.MemberService;
import ces.neighborhood.blind.app.dto.ApiResponse;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping(path = "/member/info")
    public ResponseEntity getMbrInfo(@RequestParam String mbrId) {
        return ApiResponse.success(memberService.getMbrInfo(mbrId));
    }

}
