package com.optculture.shared.entities.communication;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cs_id")
    private java.lang.Long csId;

    @Column(name = "cr_id")
    private java.lang.Long crId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "communication_id")
    private Communication communication;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "status")
    private byte status;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "channel_type")
    private String channelType;

}
