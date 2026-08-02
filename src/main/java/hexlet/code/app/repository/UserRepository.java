package hexlet.code.app.repository;

import hexlet.code.app.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByIdAndEmail(Long id, String email);

    Optional<User> findByEmail(String email);
}
