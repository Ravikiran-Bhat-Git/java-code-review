package schwarz.jobs.interview.coupon.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    @Mapping(target = "id", ignore = true)
    Coupon toEntity(CouponDTO dto);

    CouponDTO toDto(Coupon entity);
}
