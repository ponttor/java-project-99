package hexlet.code.app.service;

public interface JwtService {

    String generateToken(String email);
}
