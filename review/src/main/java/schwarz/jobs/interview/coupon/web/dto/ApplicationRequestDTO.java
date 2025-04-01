package schwarz.jobs.interview.coupon.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import schwarz.jobs.interview.coupon.core.services.model.Basket;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequestDTO {

    @NotBlank
    @Schema(description = "The coupon code", example = "XJHESWM", required = true)
    private String code;

    @NotNull
    @Schema(description = "The basket of items", example = """
                {
                    "value": 99.55
                    "appliedDiscount": 5.99
                }
            """, required = true)
    private Basket basket;

}
