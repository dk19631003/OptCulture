package com.optculture.app.repositories;

import com.optculture.app.dto.coupons.CouponInventoryDto;
import com.optculture.app.dto.coupons.CouponIssuedResponseDto;
import com.optculture.shared.entities.promotion.Coupons;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponsRepository extends JpaRepository<Coupons,Long> {

    Page<Coupons> findByUserIdAndStatusAndHighlightedOfferAndBannerImageIsNotNullOrderByCouponCreatedDate(Long userId, String running, boolean b, PageRequest of);
    @Query("SELECT new com.optculture.app.dto.coupons.CouponIssuedResponseDto(CC.couponCode as couponCode,C.status as coupCodeStatus,CC.status as couponStatus,C.couponExpiryDate as couponExpiryDate,C.couponDescription as description,CC.expiredOn as coupCodeExpiryDate,CC.issuedOn as issuedOn,C.expiryDetails as expiryDetails) FROM CouponCodes CC JOIN CC.couponId C WHERE C.couponGeneratedType = 'multiple' AND CC.orgId = :orgId AND CC.contactId = :contactId")
    Page<CouponIssuedResponseDto> findCouponsByContactId(Long contactId, Long orgId, PageRequest pageRequest);
    @Query("SELECT new com.optculture.app.dto.coupons.CouponInventoryDto(C.couponName as couponName,C.couponCode as couponCode,C.couponExpiryDate as expiryDate,C.expiryDetails as expiryDetails,C.couponDescription as description,C.couponId as couponId,C.couponGeneratedType as couponType) FROM Coupons C WHERE ( :#{#coupon.couponName} IS NULL OR C.couponName LIKE %:#{#coupon.couponName}%) AND (C.orgId = :#{#coupon.orgId}) AND (C.status IN :status)")
    Page<CouponInventoryDto> findInventoryCoupons(Coupons coupon,List<String> status, PageRequest pageRequest);

    @Query("SELECT c FROM Coupons c WHERE c.userId = :userId AND c.couponGeneratedType = 'multiple' AND c.status IN ('Active','Running') ORDER BY c.couponName")
    List<Coupons> findActiveAndRunningCouponsList(Long userId);    

    @Query("SELECT new com.optculture.app.dto.coupons.CouponInventoryDto(C.couponName as couponName,C.couponCode as couponCode,C.couponExpiryDate as expiryDate,C.expiryDetails as expiryDetails,C.couponDescription as description,C.couponId as couponId,C.couponGeneratedType as couponType) FROM Coupons C WHERE C.orgId = :orgId AND C.couponGeneratedType = 'multiple' AND C.status IN ('Active','Running') ORDER BY C.couponId")
    List<CouponInventoryDto> findActiveAndRunningCouponsDto(Long orgId);

    @Query("SELECT c FROM Coupons c WHERE c.userId = :userId AND c.status IN ('Active','Running') ORDER BY c.couponCreatedDate")
    List<Coupons> findActiveAndRunningCouponsListForBEE(Long userId);
}

