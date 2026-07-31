package hexlet.code.app.repository;

import java.util.Optional;

import hexlet.code.app.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByName(String name);
}
