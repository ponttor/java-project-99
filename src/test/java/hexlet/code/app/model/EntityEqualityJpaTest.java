package hexlet.code.app.model;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.config.JpaConfig;
import hexlet.code.app.config.PasswordConfig;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.util.ModelGenerator;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaConfig.class, PasswordConfig.class, ModelGenerator.class})
class EntityEqualityJpaTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Test
    void shouldCompareEntityWithUninitializedHibernateProxy() {
        var label = labelRepository.saveAndFlush(modelGenerator.label("feature"));
        entityManager.detach(label);
        var proxy = entityManager.getReference(Label.class, label.getId());

        assertThat(Hibernate.isInitialized(proxy)).isFalse();
        assertThat(label).isEqualTo(proxy);
        assertThat(proxy).isEqualTo(label);
        assertThat(proxy.hashCode()).isEqualTo(label.hashCode());
        assertThat(Hibernate.isInitialized(proxy)).isFalse();
    }
}
