package hexlet.code.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hexlet.code.app.exception.ResourceConflictException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class LabelServiceUnitTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelService labelService;

    @Test
    void shouldTranslateDeleteConstraintViolationIntoResourceConflict() {
        var label = new Label();
        var databaseException = new DataIntegrityViolationException("foreign key violation");
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        doThrow(databaseException).when(labelRepository).flush();

        assertThatThrownBy(() -> labelService.delete(1L)).isInstanceOf(ResourceConflictException.class)
                .hasMessage("Label is used by a task").hasCause(databaseException);

        verify(labelRepository).delete(label);
    }
}
