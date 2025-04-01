package schwarz.jobs.interview.coupon.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.exception.CouponNotFoundException;
import schwarz.jobs.interview.coupon.web.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponResource.class)
@AutoConfigureMockMvc
class CouponResourceTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    private ApplicationRequestDTO applicationRequestDTO;
    private CouponDTO couponDTO;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CouponResource(couponService)).build();
        Basket basket = new Basket(BigDecimal.valueOf(100), null, false);
        applicationRequestDTO = ApplicationRequestDTO.builder()
                .basket(basket)
                .code("SUMMER25").build();

        couponDTO = CouponDTO.builder()
                .code("SUMMER25")
                .discount(BigDecimal.valueOf(20))
                .minBasketValue(BigDecimal.valueOf(50))
                .build();
    }

    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void applyCoupon_ShouldReturnUpdatedBasket() throws Exception {
        Basket mockBasket = new Basket(BigDecimal.valueOf(80), BigDecimal.valueOf(20), true);
        when(couponService.apply(any(Basket.class), anyString())).thenReturn(mockBasket);

        mockMvc.perform(put("/api/v1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(80))
                .andExpect(jsonPath("$.applicationSuccessful").value(true));

        verify(couponService, times(1)).apply(any(Basket.class), eq("SUMMER25"));
    }


    @Test
    void applyCoupon_NotFound_ShouldReturn404() throws Exception {
        when(couponService.apply(any(Basket.class), anyString()))
                .thenThrow(new CouponNotFoundException("Coupon not found"));

        mockMvc.perform(put("/api/v1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void applyCoupon_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        ApplicationRequestDTO invalidRequest = new ApplicationRequestDTO(null, null);

        mockMvc.perform(put("/api/v1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createCoupon_ShouldReturnCreatedCoupon() throws Exception {
        when(couponService.createCoupon(any(CouponDTO.class))).thenReturn(couponDTO);

        mockMvc.perform(post("/api/v1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUMMER25"))
                .andExpect(jsonPath("$.discount").value(20));
    }

    @Test
    void createCoupon_InvalidInput_ShouldReturnBadRequest() throws Exception {
        CouponDTO invalidDTO = new CouponDTO(null, null, null);

        mockMvc.perform(post("/api/v1/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").isArray());
    }

    @Test
    void getCoupons_ShouldReturnCoupons() throws Exception {
        List<CouponDTO> mockCoupons = List.of(couponDTO);
        when(couponService.getCoupons(anyList())).thenReturn(mockCoupons);

        mockMvc.perform(get("/api/v1/coupons")
                        .param("codes", "SUMMER25,WINTER20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("SUMMER25"))
                .andExpect(jsonPath("$[0].discount").value(20));
    }

    @Test
    void getCoupons_NoCodes_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/coupons"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCoupons_EmptyResult_ShouldReturnEmptyList() throws Exception {
        when(couponService.getCoupons(anyList())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/coupons")
                        .param("codes", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

}
