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

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping(path = "/member/info")
    public ResponseEntity getMbrInfo(@RequestParam String mbrId) {
        Logger logger = LoggerFactory.getLogger(MemberController.class);

        logger.trace("Trace!");
        logger.debug("Drace!!");
        logger.info("Info!!!");
        logger.warn("Warning!!!!");
        logger.error("error!!!!!");
        return new ResponseEntity<MbrInfo>(memberService.getMbrInfo(mbrId),
                HttpStatus.OK);
    }

}
