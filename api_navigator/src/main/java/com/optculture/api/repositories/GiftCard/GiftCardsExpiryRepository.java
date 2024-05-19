package com.optculture.api.repositories.GiftCard;

import com.optculture.shared.entities.GiftCard.GiftCardsExpiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftCardsExpiryRepository extends JpaRepository<GiftCardsExpiry, Long> {

    GiftCardsExpiry findByUserIdAndGiftProgramIdAndGiftCardNumber(Long userId, Long programId, String giftCardNumber);
}