package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.neighborhood.blind.app.service.MemberService;
import ces.neighborhood.blind.app.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
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

    @PostMapping(path = "/member/info2")
    public ResponseEntity getMbrInfo2(@RequestBody String mbrId) {
        log.error("hellod");
        return ApiResponse.success(memberService.getMbrInfo(mbrId));
    }

}
