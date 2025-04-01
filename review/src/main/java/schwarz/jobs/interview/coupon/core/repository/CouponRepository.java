package schwarz.jobs.interview.coupon.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;


public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(final String code);

    @Query("""
        SELECT NEW schwarz.jobs.interview.coupon.web.dto.CouponDTO(c.discount, c.code, c.minBasketValue)
        FROM Coupon c
        WHERE c.code IN :codes
    """)
    List<CouponDTO> findAllCouponDTOsByCodesIn(List<String> codes);

}
