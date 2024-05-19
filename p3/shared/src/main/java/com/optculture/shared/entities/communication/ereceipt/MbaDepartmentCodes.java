package com.optculture.shared.entities.communication.ereceipt;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "mba_department_codes")
public class MbaDepartmentCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="department1")
    private String department1;

    @Column(name="department2")
    private String department2;

    @Column(name="score")
    private Double score;

}
