package hexlet.code.app.component;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "hexlet@example.com";

    private static final Map<String, String> DEFAULT_TASK_STATUSES = Map.of("draft", "Draft", "to_review", "To Review",
            "to_be_fixed", "To Be Fixed", "to_publish", "To Publish", "published", "Published");

    private static final Set<String> DEFAULT_LABELS = Set.of("feature", "bug");

    private final UserRepository userRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            var user = new User();
            user.setEmail(ADMIN_EMAIL);
            user.setPassword(passwordEncoder.encode("qwerty"));

            userRepository.save(user);
        }

        DEFAULT_TASK_STATUSES.forEach((slug, name) -> {
            if (taskStatusRepository.findBySlug(slug).isEmpty()) {
                var taskStatus = new TaskStatus();
                taskStatus.setName(findAvailableName(name, slug));
                taskStatus.setSlug(slug);

                taskStatusRepository.save(taskStatus);
            }
        });

        DEFAULT_LABELS.forEach(name -> {
            if (labelRepository.findByName(name).isEmpty()) {
                var label = new Label();
                label.setName(name);
                labelRepository.save(label);
            }
        });
    }

    private String findAvailableName(String preferredName, String slug) {
        if (!taskStatusRepository.existsByName(preferredName)) {
            return preferredName;
        }

        var baseName = preferredName + " (" + slug + ")";
        var availableName = baseName;
        var suffix = 2;

        while (taskStatusRepository.existsByName(availableName)) {
            availableName = baseName + " " + suffix;
            suffix++;
        }

        return availableName;
    }
}
