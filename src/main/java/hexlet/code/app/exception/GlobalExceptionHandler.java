package hexlet.code.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException exception) {
        log.warn("Resource not found: {}", exception.getMessage());
        return createResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ResourceConflictException exception) {
        log.warn("Resource conflict: {}", exception.getMessage());
        return createResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(AuthenticationException exception) {
        log.warn("Authentication failed: {}", exception.getClass().getSimpleName());
        return createResponse(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException exception) {
        log.warn("Access denied: {}", exception.getClass().getSimpleName());
        return createResponse(HttpStatus.FORBIDDEN, "Forbidden");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadableRequest(HttpMessageNotReadableException exception) {
        log.warn("Request body could not be read: {}", exception.getClass().getSimpleName());
        return createResponse(HttpStatus.BAD_REQUEST, "Malformed request");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception exception) {
        if (exception instanceof ErrorResponse errorResponse) {
            var status = errorResponse.getStatusCode();
            log.warn("Request failed with status {}: {}", status.value(), exception.getClass().getSimpleName());
            return ResponseEntity.status(status).body(errorResponse.getBody());
        }

        log.error("Unhandled exception", exception);
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ResponseEntity<ProblemDetail> createResponse(HttpStatus status, String detail) {
        var problem = ProblemDetail.forStatusAndDetail(status, detail);
        return ResponseEntity.status(status).body(problem);
    }
}
