package ces.neighborhood.blind.app.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.dto.TokenDto;
import ces.neighborhood.blind.app.record.LoginReq;
import ces.neighborhood.blind.app.service.AuthorityService;
import ces.neighborhood.blind.common.code.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Constant.BASE_API_URL)
@RequiredArgsConstructor
public class AuthorityRestController {

    private final AuthorityService authorityService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginReq loginReq) {
        TokenDto tokenDto = authorityService.authenticate(loginReq);
        return ApiResponse.success(tokenDto);
    }

}
