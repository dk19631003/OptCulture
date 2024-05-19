package com.optculture.shared.entities.unused;

import jakarta.persistence.*;

@Entity
@Table(name = "auto_program_components")
public class AutoProgramComponents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comp_id")
    private java.lang.Long compId;

    @Column(name = "support_id")
    private java.lang.Long supportId;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "footer")
    private String footer;

    @Column(name = "coordinates")
    private String coordinates;

    @Column(name = "previous_id")
    private String previousId;

    @Column(name = "next_id")
    private String nextId;

    @Column(name = "comp_type")
    private String compType;

    @Column(name = "comp_win_id")
    private String componentWinId;

    @Column(name = "stage")
    private int stage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private com.optculture.shared.entities.loyalty.AutoProgram autoProgram;

}
