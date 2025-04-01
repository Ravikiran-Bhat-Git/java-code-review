package schwarz.jobs.interview.coupon.core.services.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Basket {

    @NotNull
    @Schema(description = "Total value of the basket", example = "99.95", required = true)
    private BigDecimal value;
    @Schema(description = "Discount amount applied to the basket", example = "5.99", required = true)
    private BigDecimal appliedDiscount;
    @Schema(description = "Indicates whether the last discount application was successful", example = "true", required = true)
    private boolean applicationSuccessful;

    public void applyDiscount(final BigDecimal discount) {
        this.applicationSuccessful = false;
        this.appliedDiscount = null;

        if (discount == null || discount.compareTo(BigDecimal.ZERO) < 0) {
            return;
        }

        if (this.value.compareTo(discount) >= 0) {
            this.value = this.value.subtract(discount);
            this.appliedDiscount = discount;
            this.applicationSuccessful = true;
        }
    }

}
