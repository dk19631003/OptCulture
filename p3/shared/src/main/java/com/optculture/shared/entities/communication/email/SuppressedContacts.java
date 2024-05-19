package com.optculture.shared.entities.communication.email;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "suppressed_contacts")
@Data
public class SuppressedContacts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "type")
    private String type;

    @Column(name = "source")
    private String source;

    @Column(name = "suppressed_time")
    private LocalDateTime suppressedtime;

    @Column(name = "reason", length = 2000)
    private String reason;

    @Column(name = "user_id")
    private java.lang.Long userId;

}
