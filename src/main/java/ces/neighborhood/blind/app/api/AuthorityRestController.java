package ces.neighborhood.blind.app.api;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.record.LoginReq;
import ces.neighborhood.blind.app.service.AuthorityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthorityRestController {

    private final AuthorityService authorityService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginReq loginReq) {
        authorityService.authenticate(loginReq);

        return ApiResponse.success();
    }

}
