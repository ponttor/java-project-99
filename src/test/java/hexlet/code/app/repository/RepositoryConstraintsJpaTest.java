package hexlet.code.app.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hexlet.code.app.config.JpaConfig;
import hexlet.code.app.config.PasswordConfig;
import hexlet.code.app.util.ModelGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@Import({JpaConfig.class, PasswordConfig.class, ModelGenerator.class})
class RepositoryConstraintsJpaTest {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Test
    void shouldFindResourcesByTheirNaturalKeys() {
        var label = labelRepository.save(modelGenerator.label("feature"));
        var status = taskStatusRepository.save(modelGenerator.taskStatus("Draft", "draft"));
        var user = userRepository.save(modelGenerator.user("user@example.com"));

        assertThat(labelRepository.findByName("feature")).contains(label);
        assertThat(taskStatusRepository.findBySlug("draft")).contains(status);
        assertThat(userRepository.findByEmail("user@example.com")).contains(user);
    }

    @Test
    void shouldRejectDuplicateLabelName() {
        labelRepository.saveAndFlush(modelGenerator.label("feature"));
        var duplicateLabel = modelGenerator.label("feature");

        assertThatThrownBy(() -> labelRepository.saveAndFlush(duplicateLabel))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectDuplicateTaskStatusName() {
        taskStatusRepository.saveAndFlush(modelGenerator.taskStatus("Draft", "draft"));
        var duplicateStatusName = modelGenerator.taskStatus("Draft", "another_slug");

        assertThatThrownBy(() -> taskStatusRepository.saveAndFlush(duplicateStatusName))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectDuplicateTaskStatusSlug() {
        taskStatusRepository.saveAndFlush(modelGenerator.taskStatus("Draft", "draft"));
        var duplicateStatusSlug = modelGenerator.taskStatus("Another name", "draft");

        assertThatThrownBy(() -> taskStatusRepository.saveAndFlush(duplicateStatusSlug))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectDuplicateUserEmail() {
        userRepository.saveAndFlush(modelGenerator.user("user@example.com"));
        var duplicateUser = modelGenerator.user("user@example.com");

        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicateUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
