package schwarz.jobs.interview.coupon.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidCouponException extends RuntimeException {
    public InvalidCouponException(String message) {
        super(message);
    }
}
