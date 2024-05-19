package com.optculture.api.repositories.GiftCard;

import com.optculture.shared.entities.GiftCard.GiftCardsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCardsHistoryRepository extends JpaRepository<GiftCardsHistory, Long> {
    GiftCardsHistory findByHistoryId(Long historyId);

    @Query("SELECT gh FROM GiftCardsHistory gh WHERE gh.userId = :userId AND gh.giftCardNumber = :giftCardNumber AND" +
            " gh.receiptNumber = :receiptNumber AND gh.storeNumber = :storeNumber ")
    List<GiftCardsHistory> findByUserIdAndGiftCardNumberAndReceiptNumberAndStoreNumber(@Param("userId") Long userId,
                                                                        @Param("giftCardNumber") String giftCardNumber,
                                                                        @Param("receiptNumber") String receiptNumber,
                                                                        @Param("storeNumber") String storeNumber);
}