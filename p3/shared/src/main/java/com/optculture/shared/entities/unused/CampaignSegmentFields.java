package com.optculture.shared.entities.unused;

import jakarta.persistence.*;

@Entity
@Table(name = "campaign_segment_fields")
public class CampaignSegmentFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "seg_condition")
    private String condition;

    @Column(name = "param_one")
    private String paramOne;

    @Column(name = "param_two")
    private String paramTwo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private com.optculture.shared.entities.communication.email.Campaigns campaignId;

}
