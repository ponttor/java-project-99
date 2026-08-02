package hexlet.code.app.service;

import hexlet.code.app.dto.label.LabelCreateRequest;
import hexlet.code.app.dto.label.LabelResponse;
import hexlet.code.app.dto.label.LabelUpdateRequest;
import hexlet.code.app.exception.ResourceConflictException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelService {

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    @Transactional(readOnly = true)
    public List<LabelResponse> findAll() {
        return labelMapper.toResponses(labelRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LabelResponse findById(Long id) {
        return labelMapper.toResponse(findLabel(id));
    }

    public LabelResponse create(LabelCreateRequest request) {
        ensureNameAvailable(request.getName(), null);
        var label = labelMapper.toEntity(request);
        return save(label);
    }

    public LabelResponse update(Long id, LabelUpdateRequest request) {
        var label = findLabel(id);
        ensureNameAvailable(request.getName(), label.getId());
        labelMapper.update(request, label);
        return save(label);
    }

    public void delete(Long id) {
        try {
            labelRepository.delete(findLabel(id));
            labelRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceConflictException("Label is used by a task", exception);
        }
    }

    private Label findLabel(Long id) {
        return labelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Label not found"));
    }

    private void ensureNameAvailable(String name, Long currentLabelId) {
        labelRepository.findByName(name).filter(label -> !label.getId().equals(currentLabelId)).ifPresent(label -> {
            throw new ResourceConflictException("Label name already exists");
        });
    }

    private LabelResponse save(Label label) {
        try {
            return labelMapper.toResponse(labelRepository.saveAndFlush(label));
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceConflictException("Label name already exists", exception);
        }
    }
}
