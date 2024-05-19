package com.optculture.shared.entities.GiftCard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_card_skus", indexes = {
        @Index(name = "USERID_INDEX", columnList = "user_id"),
        @Index(name = "SKUCDEUSER_INDEX", columnList = "user_id,sku_code")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GiftCardSkus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sku_id")
    private java.lang.Long skuId;

    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "sku_name")
    private String skuName;

    @ManyToOne
    @JoinColumn(name = "gift_program_id", referencedColumnName = "gift_program_id")
    private GiftPrograms giftPrograms;

    @Column(name = "category")
    private String category;

    @Column(name = "fixed_amount")
    private java.lang.Double fixedAmount;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
