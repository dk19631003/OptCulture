package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "user_score_settings")
public class UserScoreSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "groupName_cnd")
    private String condition;

    @Column(name = "data1")
    private String dataOne;

    @Column(name = "data2")
    private String dataTwo;

    @Column(name = "score", length = 20)
    private java.lang.Integer score;

    @Column(name = "max_score")
    private java.lang.Integer maxScore;

    @Column(name = "type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}
