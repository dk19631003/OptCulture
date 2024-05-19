package com.optculture.api.repositories.GiftCard;

import com.optculture.shared.entities.GiftCard.GiftCardSkus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftCardSkusRepository extends JpaRepository<GiftCardSkus,Long> {
    GiftCardSkus findByUserIdAndSkuCode(Long userId,String skuCode);
}
