package ces.neighborhood.blind.app.api;

import ces.neighborhood.blind.common.code.Constant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = Constant.BASE_URL + "/auth")
public class AuthorityRestController {


    public ResponseEntity login () {
        return null;
    }
}
