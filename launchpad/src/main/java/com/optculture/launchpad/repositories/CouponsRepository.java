package com.optculture.launchpad.repositories;


import com.optculture.shared.entities.communication.Communication;
//import com.optculture.app.dto.coupons.CouponInventoryDto;
//import com.optculture.app.dto.coupons.CouponIssuedResponseDto;
import com.optculture.shared.entities.promotion.Coupons;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponsRepository extends JpaRepository<Coupons,Long> {

    Page<Coupons> findByUserIdAndStatusAndHighlightedOfferAndBannerImageIsNotNullOrderByCouponCreatedDate(Long userId, String running, boolean b, PageRequest of);
   
    Optional<Coupons> findById(Long id);

    List<Coupons> findByCouponIdIn(List<Long> couponIds);


    //@Query("SELECT new com.optculture.app.dto.coupons.CouponIssuedResponseDto(CC.couponCode as couponCode,C.status as coupCodeStatus,CC.status as couponStatus,C.couponExpiryDate as couponExpiryDate,C.couponDescription as description,CC.expiredOn as coupCodeExpiryDate,CC.issuedOn as issuedOn,C.expiryDetails as expiryDetails) FROM CouponCodes CC JOIN CC.couponId C WHERE CC.orgId = :orgId AND CC.contactId = :contactId")
    //Page<CouponIssuedResponseDto> findCouponsByContactId(Long contactId, Long orgId, PageRequest pageRequest);
 
   // @Query("SELECT new com.optculture.app.dto.coupons.CouponInventoryDto(C.couponCode as couponCode,C.couponExpiryDate as expiryDate,C.expiryDetails as expiryDetails,C.couponDescription as description,CC.ccId as couponCodeId) FROM CouponCodes CC JOIN CC.couponId C WHERE CC.orgId = :orgId AND C.status = :couponStatus AND CC.status = :couponCodeStatus GROUP BY C.couponId ")
   // Page<CouponInventoryDto> findInventoryCoupons(String couponStatus, String couponCodeStatus, Long orgId,PageRequest pageRequest);
}
