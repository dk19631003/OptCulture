package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "loyalty_balance")
public class LoyaltyBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long lbId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "value_code")
    private String valueCode;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "loyalty_id")
    private Long loyaltyId;

    @Column(name = "membership_number")
    private String memberShipNumber;

    @Column(name = "total_earn_balance")
    private Double totalEarnedBalance;

    @Column(name = "total_redeemed_balance")
    private Double totalRedeemedBalance;

    @Column(name = "total_expired_balance")
    private Double totalExpiredBalance;

    @Column(name = "program_id")
    private Long programId;

}
