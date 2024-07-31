package ces.neighborhood.blind.app.controller;

import ces.neighborhood.blind.app.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class IndexController {

    @GetMapping("/index")
    public ResponseEntity getMenu() {


        return ApiResponse.success();
    }

}
