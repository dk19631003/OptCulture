package com.optculture.shared.entities.GiftCard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "gift_cards", indexes = {
        @Index(name="USER_INDEX", columnList="user_id"),
        @Index(name="PIN_INDEX", columnList = "gift_card_pin"),
        @Index(name="GIFTPRGMID_INDEX", columnList = "gift_program_id"),
        @Index(name="CARDNUMUSER_INDEX", columnList = "user_id,gift_card_number"),
        @Index(name="USERGIFTMOBILE_INDEX", columnList = "user_id,gifted_to_mobile"),
        @Index(name="USERGFTEMIL_INDEX", columnList = "user_id,gifted_to_email"),
        @Index(name="USERPURCMOBILE_INDEX", columnList = "user_id,purchased_mobile"),
        @Index(name="USERPURCEMIL_INDEX", columnList = "user_id,purchased_email")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GiftCards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gift_card_id")
    private java.lang.Long giftCardId;

    @Column(name="user_id")
    private java.lang.Long userId;

    @Column(name="gift_program_id")
    private java.lang.Long giftProgramId;

    @Column(name = "gift_card_number")
    private String giftCardNumber;

    @Column(name = "gift_card_pin")
    private String giftCardPin;

    @Column(name = "gift_card_status")
    private String giftCardStatus;

    @Column(name="gift_balance")
    private java.lang.Double giftBalance;

    @Column(name="total_loaded")
    private java.lang.Double totalLoaded;

    @Column(name="total_redeemed")
    private java.lang.Double totalRedeemed;

    @Column(name="total_expired")
    private java.lang.Double totalExpired;

    @Column(name = "purchased_mobile")
    private java.lang.Long purchasedMobile;

    @Column(name = "purchased_email")
    private String purchasedEmail;

    @Column(name="purchased_date")
    private LocalDateTime purchasedDate;

    @Column(name = "purchased_item_sid")
    private String purchasedItemSid;

    @Column(name="purchased_store_id")
    private java.lang.Long purchasedStoreId;

    @Column(name = "gifted_to_mobile")
    private java.lang.Long giftedToMobile;

    @Column(name = "gifted_to_email")
    private String giftedToEmail;

    @Column(name="gifted_date")
    private LocalDateTime giftedDate;

    @Column(name="expiry_date")
    private LocalDateTime expiryDate;

}
