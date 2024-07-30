package ces.neighborhood.blind.app.controller;

import ces.neighborhood.blind.app.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public ResponseEntity getMenu() {


        return ApiResponse.success();
    }

}
