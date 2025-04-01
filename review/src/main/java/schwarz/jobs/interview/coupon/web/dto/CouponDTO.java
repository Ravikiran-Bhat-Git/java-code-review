package schwarz.jobs.interview.coupon.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {
    @NotNull
    @Schema(description = "The applicable discount value", example = "5.99", required = true)
    private BigDecimal discount;
    @NotBlank
    @Schema(description = "The coupon code", example = "XJHESWM", required = true)
    private String code;
    @NotNull
    @Positive
    @Schema(description = "Minimum basket value eligible for discount", example = "19.99", required = true)
    private BigDecimal minBasketValue;

}
