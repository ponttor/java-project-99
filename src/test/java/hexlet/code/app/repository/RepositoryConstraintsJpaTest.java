package hexlet.code.app.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hexlet.code.app.config.JpaConfig;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@Import(JpaConfig.class)
class RepositoryConstraintsJpaTest {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindResourcesByTheirNaturalKeys() {
        var label = labelRepository.save(label("feature"));
        var status = taskStatusRepository.save(taskStatus("Draft", "draft"));
        var user = userRepository.save(user("user@example.com"));

        assertThat(labelRepository.findByName("feature")).contains(label);
        assertThat(taskStatusRepository.findBySlug("draft")).contains(status);
        assertThat(userRepository.findByEmail("user@example.com")).contains(user);
    }

    @Test
    void shouldRejectDuplicateLabelName() {
        labelRepository.saveAndFlush(label("feature"));
        var duplicateLabel = label("feature");

        assertThatThrownBy(() -> labelRepository.saveAndFlush(duplicateLabel))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectDuplicateTaskStatusName() {
        taskStatusRepository.saveAndFlush(taskStatus("Draft", "draft"));
        var duplicateStatusName = taskStatus("Draft", "another_slug");

        assertThatThrownBy(() -> taskStatusRepository.saveAndFlush(duplicateStatusName))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectDuplicateTaskStatusSlug() {
        taskStatusRepository.saveAndFlush(taskStatus("Draft", "draft"));
        var duplicateStatusSlug = taskStatus("Another name", "draft");

        assertThatThrownBy(() -> taskStatusRepository.saveAndFlush(duplicateStatusSlug))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectDuplicateUserEmail() {
        userRepository.saveAndFlush(user("user@example.com"));
        var duplicateUser = user("user@example.com");

        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicateUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Label label(String name) {
        var label = new Label();
        label.setName(name);
        return label;
    }

    private TaskStatus taskStatus(String name, String slug) {
        var status = new TaskStatus();
        status.setName(name);
        status.setSlug(slug);
        return status;
    }

    private User user(String email) {
        var user = new User();
        user.setEmail(email);
        user.setPassword("password");
        return user;
    }
}
