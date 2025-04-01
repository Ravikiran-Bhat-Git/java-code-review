package schwarz.jobs.interview.coupon.web.dto;

import java.util.List;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponRequestDTO {

    @NotNull
    @Schema(description = "List of coupon codes", example = "[\"XJHESWM\", \"KSVJSFVJ\"]", required = true)
    private List<String> codes;

}
