package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.dto.Oauth2Authentication;
import ces.neighborhood.blind.app.service.authority.OAuthService;
import ces.neighborhood.blind.common.code.Constant;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constant.BASE_API_URL + "/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/{registrationId}")
    public ResponseEntity oauthLogin(@PathVariable String registrationId, @RequestParam String code, @RequestParam String state) {
        Oauth2Authentication authentication = oAuthService.authenticate(registrationId, code, state);
        return ApiResponse.success(authentication.getTokenDto(), authentication.getOAuthLoginRes());
    }

}
