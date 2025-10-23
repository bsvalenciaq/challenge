package co.com.challenge.logintokengeneratorcontroller;

import co.com.challenge.model.utils.interfaces.JwtInterface;
import co.com.challenge.model.utils.responses.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginTokenGeneratorController {
    private final JwtInterface jwtInterface;

    @GetMapping("/get-login-token")
    public ResponseEntity<ResponseUtil> getLoginToken() {
        // For demonstration purposes, we return a static token.

        var token = jwtInterface.generateToken();
        return new ResponseEntity<>(new ResponseUtil(200, "Token generated successfully", token),
                org.springframework.http.HttpStatus.OK);
    }
}
