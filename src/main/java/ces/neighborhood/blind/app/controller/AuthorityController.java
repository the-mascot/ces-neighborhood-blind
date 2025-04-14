package ces.neighborhood.blind.app.controller;

import ces.neighborhood.blind.app.dto.authority.CesAuthentication;
import ces.neighborhood.blind.app.dto.authority.JoinReqDto;
import ces.neighborhood.blind.app.dto.authority.LoginReqDto;
import ces.neighborhood.blind.app.dto.common.ApiResponse;
import ces.neighborhood.blind.app.record.authority.LoginRes;
import ces.neighborhood.blind.app.service.authority.AuthorityService;
import ces.neighborhood.blind.common.constant.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.BASE_API_URL + "/auth")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginRes>> login(@Valid @RequestBody LoginReqDto loginReq) {
        CesAuthentication cesAuthentication = authorityService.authenticate(loginReq);
        return ApiResponse.success(cesAuthentication.getTokenDto(), cesAuthentication.getLoginRes());
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(@Valid @RequestBody JoinReqDto joinReq) {
        authorityService.joinMember(joinReq);
        return ApiResponse.success();
    }
}
