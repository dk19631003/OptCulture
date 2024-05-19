package com.optculture.app.repositories;

import com.optculture.shared.entities.promotion.CouponCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponCodesRepository extends JpaRepository<CouponCodes,Long> {
    @Query("SELECT COUNT(*) FROM CouponCodes cc WHERE cc.couponId.id = :couponId")
    long findCountByCouponId(@Param("couponId") Long couponId);

    Optional<CouponCodes> findFirstByCouponIdCouponIdAndStatus(Long couponId, String inventory);
}
