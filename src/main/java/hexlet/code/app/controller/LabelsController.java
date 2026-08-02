package hexlet.code.app.controller;

import hexlet.code.app.dto.label.LabelCreateRequest;
import hexlet.code.app.dto.label.LabelResponse;
import hexlet.code.app.dto.label.LabelUpdateRequest;
import hexlet.code.app.service.LabelService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelsController {

    private final LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelResponse>> index() {
        var labels = labelService.findAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(labels.size())).body(labels);
    }

    @GetMapping("/{id}")
    public LabelResponse show(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponse create(@Valid @RequestBody LabelCreateRequest request) {
        return labelService.create(request);
    }

    @PutMapping("/{id}")
    public LabelResponse update(@PathVariable Long id, @Valid @RequestBody LabelUpdateRequest request) {
        return labelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        labelService.delete(id);
    }
}
