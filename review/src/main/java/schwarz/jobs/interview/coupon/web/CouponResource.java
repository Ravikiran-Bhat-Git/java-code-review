package schwarz.jobs.interview.coupon.web;


import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.ApiError;
import schwarz.jobs.interview.coupon.web.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
@Slf4j
public class CouponResource {

    private final CouponService couponService;

    @PutMapping
    @Tag(name = "Coupon management")
    @Operation(summary = "Apply coupon to basket", description = "Applies a coupon to the specified basket", operationId = "applyDiscount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = Basket.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<Basket> apply(
        @Parameter(description = "Coupon application request", required = true)
        @RequestBody @Valid final ApplicationRequestDTO applicationRequestDTO) {

        log.info("Applying coupon");
        return ResponseEntity.ok().body(couponService.apply(applicationRequestDTO.getBasket(), applicationRequestDTO.getCode()));
    }

    @PostMapping
    @Tag(name = "Coupon management")
    @Operation(summary = "Create new coupon", description = "Create a new coupon", operationId = "createCoupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = Basket.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<CouponDTO> create(
            @Parameter(description = "Coupon creation data", required = true)
            @RequestBody @Valid final CouponDTO couponDTO) {
        return ResponseEntity.ok(couponService.createCoupon(couponDTO));
    }

    @GetMapping
    @Tag(name = "Coupon management")
    @Operation(summary = "Get coupons by codes", description = "Get coupons by codes", operationId = "getCouponsByCodes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = Basket.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public ResponseEntity<List<CouponDTO>> getCoupons(
            @Parameter(description = "List of coupon codes to retrieve", required = true)
            @RequestParam("codes") List<String> codes) {
        log.info("Fetching coupons for codes: {}", codes);
        List<CouponDTO> coupons = couponService.getCoupons(codes);
        return ResponseEntity.ok(coupons);
    }
}
