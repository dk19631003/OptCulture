package com.optculture.launchpad.repositories;
import com.optculture.shared.entities.promotion.CouponCodes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponCodesRepository extends JpaRepository<CouponCodes,Long> {

	
	CouponCodes findByCouponIdCouponIdAndContactId(Long couponId,Long contactId);

	
	List<CouponCodes> findByCouponIdCouponIdAndStatus(Long couponId,String status);
	
	CouponCodes findByCouponCodeAndOrgId(String couponCode,Long orgId);

	@Query("SELECT COUNT(*) FROM CouponCodes cc WHERE cc.couponId.id = :couponId")
	long findCountByCouponId(@Param("couponId") Long couponId);


	List<CouponCodes> findByCouponIdCouponIdAndContactIdAndStatus(Long couponId, Long contactID, String status);

    List<CouponCodes> findByOrgIdAndCouponCodeIn(Long orgId, List<String> couponCodes);
}
