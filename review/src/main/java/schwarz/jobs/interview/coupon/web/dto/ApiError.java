package schwarz.jobs.interview.coupon.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class ApiError {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<ValidationError> validationErrors;

    @Getter
    @Setter
    @Builder
    public static class ValidationError {
        private String field;
        private String message;
        private String rejectedValue;
    }
}