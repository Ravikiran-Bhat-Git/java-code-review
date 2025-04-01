package schwarz.jobs.interview.coupon.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidBasketException extends RuntimeException {
    public InvalidBasketException(String message) {
        super(message);
    }
}
