package hexlet.code.app.service;

import java.util.List;

import hexlet.code.app.dto.LabelCreateRequest;
import hexlet.code.app.dto.LabelResponse;
import hexlet.code.app.dto.LabelUpdateRequest;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelService {

    private final LabelRepository labelRepository;

    @Transactional(readOnly = true)
    public List<LabelResponse> findAll() {
        return labelRepository.findAll().stream()
                .map(LabelResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public LabelResponse findById(Long id) {
        return new LabelResponse(findLabel(id));
    }

    public LabelResponse create(LabelCreateRequest request) {
        ensureNameAvailable(request.getName(), null);
        var label = new Label();
        label.setName(request.getName());
        return save(label);
    }

    public LabelResponse update(Long id, LabelUpdateRequest request) {
        var label = findLabel(id);
        ensureNameAvailable(request.getName(), label.getId());
        label.setName(request.getName());
        return save(label);
    }

    public void delete(Long id) {
        try {
            labelRepository.delete(findLabel(id));
            labelRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Label is used by a task", exception);
        }
    }

    private Label findLabel(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
    }

    private void ensureNameAvailable(String name, Long currentLabelId) {
        labelRepository.findByName(name)
                .filter(label -> !label.getId().equals(currentLabelId))
                .ifPresent(label -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Label name already exists");
                });
    }

    private LabelResponse save(Label label) {
        try {
            return new LabelResponse(labelRepository.saveAndFlush(label));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Label name already exists", exception);
        }
    }
}
