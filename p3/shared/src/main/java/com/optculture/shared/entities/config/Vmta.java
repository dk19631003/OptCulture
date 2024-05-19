package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "vmta")
public class Vmta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "vmta_name", length = 64)
    private String vmtaName;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name = "pooled_vmtas")
    private String pooledVmtas;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_api_name")
    private String accountAPIName;

    @Column(name = "account_pwd")
    private String accountPwd;

    @Column(name = "host")
    private String host;

}
