package hexlet.code.app.service;

import hexlet.code.app.dto.label.LabelCreateRequest;
import hexlet.code.app.dto.label.LabelResponse;
import hexlet.code.app.dto.label.LabelUpdateRequest;
import java.util.List;

public interface LabelService {

    List<LabelResponse> findAll();

    LabelResponse findById(Long id);

    LabelResponse create(LabelCreateRequest request);

    LabelResponse update(Long id, LabelUpdateRequest request);

    void delete(Long id);
}
