package co.com.challenge.model.utils.interfaces;

public interface JwtInterface {
    String generateToken();

    boolean validateToken(String token);
}
