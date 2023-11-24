package ces.neighborhood.blind.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.service.MemberService;
import ces.neighborhood.blind.common.entity.ApiResponse;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

//    @GetMapping(path = "/member/info")
//    public ResponseEntity getMbrInfo(@RequestParam String mbrId) {
//        return new ResponseEntity<MbrInfo>(memberService.getMbrInfo(mbrId),
//                HttpStatus.OK);
//    }

    @GetMapping(path = "/member/info")
    public ResponseEntity getMbrInfo(@RequestParam String mbrId) {
        return ApiResponse.success(memberService.getMbrInfo(mbrId));
    }

}
