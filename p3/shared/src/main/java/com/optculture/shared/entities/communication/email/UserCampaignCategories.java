package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "user_campaign_categories")
public class UserCampaignCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "category_name", length = 50)
    private String categoryName;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "parent_str", length = 50)
    private String parentStr;

    @Column(name = "is_visible", length = 50)
    private boolean isVisible;

    @Column(name = "user_id", length = 50)
    private java.lang.Long userId;

    @Column(name = "created_date", length = 50)
    private java.util.Calendar createdDate;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "parent_position_id")
    private long parentPositionId;

}
