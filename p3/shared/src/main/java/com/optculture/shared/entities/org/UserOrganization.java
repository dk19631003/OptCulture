package com.optculture.shared.entities.org;

import java.util.Calendar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_organization")
public class UserOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_org_id")
    private Long userOrgId;

    @Column(name = "org_name")
    private String organizationName;

    @Column(name = "org_external_id")
    private String orgExternalId;

    @Column(name = "created_date")
    private Calendar createdDate;

    @Column(name = "branding")
    private String branding;

    @Column(name = "client_type")
    private String clientType;

    @Column(name = "msg_received_number")
    private String msgReceivingNumbers;

    @Column(name = "to_email_id")
    private String toEmailId;

    @Column(name = "loyalty_display_template")
    private String loyaltyDisplayTemplate;

    @Column(name = "FBP_template")
    private String FBPTemplate;

    @Column(name = "opt_sync_key")
    private String optSyncKey;

    @Column(name = "org_status", length = 16)
    private String orgStatus;

    @Column(name = "max_keywords")
    private Integer maxKeywords;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "nextcard_seqno")
    private Long nextCardSeqNo;

    @Column(name = "cardgenerate_flag", length = 16)
    private String cardGenerateFlag;

    @Column(name = "cardseq_prefix")
    private Long cardSeqPrefix;

    @Column(name = "cardrand_prefix")
    private Long cardRandPrefix;

    @Column(name = "min_number_of_digits")
    private Integer minNumberOfDigits;

    @Column(name = "max_number_of_digits")
    private Integer maxNumberOfDigits;

    @Column(name = "mobile_pattern")
    private Boolean mobilePattern;

    @Column(name = "require_mobile_validation")
    private Boolean requireMobileValidation;

    @Column(name = "cross_program_card_transfer")
    private Boolean crossProgramCardTransfer;

    @Column(name = "suspended_program_transfer")
    private Boolean suspendedProgramTransfer;

    @Column(name = "multi_user")
    private Boolean multiUser;

    @Column(name = "banner_image_path")
    private String bannerPath;

    @Column(name = "send_realtime_loyalty_status")
    private Boolean sendRealtimeLoyaltyStatus;

}
