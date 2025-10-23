package co.com.challenge.healthcontroller;

import co.com.challenge.model.utils.responses.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping(path = "/health")
    public ResponseEntity<ResponseUtil> serviceOk() {
        return new ResponseEntity<>(new ResponseUtil(200, "OK",
                "Services Online"), HttpStatus.OK);
    }
}