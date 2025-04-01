package schwarz.jobs.interview.coupon.core.services;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.exception.CouponNotFoundException;
import schwarz.jobs.interview.coupon.exception.InvalidBasketException;
import schwarz.jobs.interview.coupon.exception.InvalidCouponException;
import schwarz.jobs.interview.coupon.mapper.CouponMapper;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    public Coupon getCoupon(final String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with code: " + code));
    }

    public Basket apply(Basket basket, final String code) {
        final Coupon coupon = getCoupon(code);
        validateCouponApplicability(basket, coupon);

        BigDecimal discount = coupon.getDiscount();
        if (basket.getValue().compareTo(coupon.getMinBasketValue()) > 0) {
            basket.applyDiscount(discount);

            log.debug("Applied discount {} to basket", discount);
            return basket;
        }

        return basket;

    }

    private void validateCouponApplicability(Basket basket, Coupon coupon) {
        if (coupon.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Attempted to apply negative discount from coupon {}", coupon.getCode());
            throw new InvalidCouponException("Coupon has invalid negative discount");
        }

        if (basket.getValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBasketException(
                    "Invalid Basket value: " + basket.getValue() + ". Basket value cannot be negative");
        }
    }

    @Transactional
    public CouponDTO createCoupon(final CouponDTO couponDTO) {
        Coupon savedCoupon = couponRepository.save(couponMapper.toEntity(couponDTO));
        return couponMapper.toDto(savedCoupon);
    }

    public List<CouponDTO> getCoupons(final List<String> codes) {
        if (codes.isEmpty()) {
            return List.of();
        }
        return couponRepository.findAllCouponDTOsByCodesIn(codes);
    }
}
