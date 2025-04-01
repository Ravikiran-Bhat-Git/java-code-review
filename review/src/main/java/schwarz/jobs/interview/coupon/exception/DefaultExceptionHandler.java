package schwarz.jobs.interview.coupon.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import schwarz.jobs.interview.coupon.web.dto.ApiError;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ApiError.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ApiError.ValidationError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue() != null ?
                                fieldError.getRejectedValue().toString() : null)
                        .build())
                .toList();

        ApiError apiError = buildApiError(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                validationErrors
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler({
            CouponNotFoundException.class,
            InvalidCouponException.class,
            InvalidBasketException.class
    })
    public ResponseEntity<ApiError> handleCustomExceptions(
            RuntimeException ex,
            WebRequest request
    ) {
        HttpStatus status = determineHttpStatus(ex);
        ApiError apiError = buildApiError(status, ex.getMessage(), request);
        return ResponseEntity.status(status).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(
            Exception ex,
            WebRequest request
    ) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ApiError apiError = buildApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request
        );
        return ResponseEntity.internalServerError().body(apiError);
    }

    private HttpStatus determineHttpStatus(RuntimeException ex) {
        return ex instanceof CouponNotFoundException ?
                HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
    }

    private ApiError buildApiError(
            HttpStatus status,
            String message,
            WebRequest request,
            List<ApiError.ValidationError> validationErrors
    ) {
        return ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .validationErrors(validationErrors)
                .build();
    }

    private ApiError buildApiError(
            HttpStatus status,
            String message,
            WebRequest request
    ) {
        return buildApiError(status, message, request, null);
    }
}
