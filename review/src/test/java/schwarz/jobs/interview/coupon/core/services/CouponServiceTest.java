package schwarz.jobs.interview.coupon.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.exception.InvalidBasketException;
import schwarz.jobs.interview.coupon.mapper.CouponMapper;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponMapper couponMapper;

    @Test
    public void createCoupon() {
        CouponDTO dto = CouponDTO.builder()
            .code("12345")
            .discount(BigDecimal.TEN)
            .minBasketValue(BigDecimal.valueOf(50))
            .build();
        Coupon entity = Coupon.builder()
                .code("12345")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();
        when(couponMapper.toEntity(dto)).thenReturn(entity);
        when(couponRepository.save(any(Coupon.class))).thenReturn(entity);
        couponService.createCoupon(dto);

        verify(couponRepository, times(1)).save(any());
        verify(couponMapper, times(1)).toEntity(dto);
    }

    @Test
    public void test_apply_coupon_method() {

        final Basket firstBasket = Basket.builder()
            .value(BigDecimal.valueOf(100))
            .build();

        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
            .code("1111")
            .discount(BigDecimal.TEN)
            .minBasketValue(BigDecimal.valueOf(50))
            .build()));

        Basket basket = couponService.apply(firstBasket, "1111");

        assertThat(basket.getAppliedDiscount()).isEqualTo(BigDecimal.TEN);
        assertThat(basket.isApplicationSuccessful()).isTrue();

        final Basket secondBasket = Basket.builder()
            .value(BigDecimal.valueOf(0))
            .build();


        basket = couponService.apply(secondBasket, "1111");
        assertThat(basket).isEqualTo(secondBasket);
        assertThat(basket.isApplicationSuccessful()).isFalse();

        final Basket thirdBasket = Basket.builder()
            .value(BigDecimal.valueOf(-1))
            .build();

        assertThatThrownBy(() -> {
            couponService.apply(thirdBasket, "1111");
        }).isInstanceOf(InvalidBasketException.class)
            .hasMessage("Invalid Basket value: -1. Basket value cannot be negative");
    }

    @Test
    public void should_test_get_Coupons() {
        CouponDTO firstCoupon = CouponDTO.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();
        CouponDTO secondCoupon = CouponDTO.builder()
                .code("1234")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        when(couponRepository.findAllCouponDTOsByCodesIn(anyList()))
            .thenReturn(List.of(firstCoupon, secondCoupon));

        List<CouponDTO> returnedCoupons = couponService.getCoupons(List.of("1111", "1234"));

        assertThat(returnedCoupons).containsAll(List.of(firstCoupon, secondCoupon));
    }
}
