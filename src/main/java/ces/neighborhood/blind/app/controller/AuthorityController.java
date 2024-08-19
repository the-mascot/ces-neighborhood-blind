package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.dto.CesAuthentication;
import ces.neighborhood.blind.app.record.authority.JoinReq;
import ces.neighborhood.blind.app.record.authority.LoginReq;
import ces.neighborhood.blind.app.service.authority.AuthorityService;
import ces.neighborhood.blind.app.service.authority.UserDetailServiceImpl;
import ces.neighborhood.blind.common.code.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Constant.BASE_API_URL + "/auth")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    private final UserDetailServiceImpl userDetailService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginReq loginReq) {
        CesAuthentication cesAuthentication = authorityService.authenticate(loginReq);
        return ApiResponse.success(cesAuthentication.getTokenDto(), cesAuthentication.getLoginRes());
    }

    @PostMapping("/join")
    public ResponseEntity join(@Valid @RequestBody JoinReq joinReq) {
        authorityService.joinMember(joinReq);
        return ApiResponse.success();
    }
}
