package com.optculture.api.repositories.GiftCard;

import com.optculture.shared.entities.GiftCard.GiftCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftCardsRepository extends JpaRepository<GiftCards, Long> {
    GiftCards findFirstByUserIdAndGiftCardNumber(Long userId, String giftCardNumber);
}
