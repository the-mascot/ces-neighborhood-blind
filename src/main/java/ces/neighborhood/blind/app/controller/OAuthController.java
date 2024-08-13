package ces.neighborhood.blind.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.service.OAuthService;
import ces.neighborhood.blind.common.code.Constant;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constant.BASE_API_URL + "/login/oauth2/code")
public class OAuthController {

    private OAuthService oAuthService;

    @GetMapping("/naver")
    public ApiResponse naverLogin(@RequestParam String code, @RequestParam String state) {
        oAuthService.authenticate(code, state);

        return null;
    }

}
