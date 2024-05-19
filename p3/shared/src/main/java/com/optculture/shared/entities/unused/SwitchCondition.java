package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "switch_condition")
public class SwitchCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "switch_cond_id")
    private java.lang.Long switchCondId;

    @Column(name = "condition_query")
    private String conditionQuery;

    @Column(name = "component_id")
    private java.lang.Long componentId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "msg_line")
    private String lineMessage;

    @Column(name = "mode_attribute")
    private String modeAttribute;

    @Column(name = "condition_new")
    private String condition;

    @Column(name = "switch_comp_winid")
    private String switchCompWinId;

    @Column(name = "mode_flag")
    private String modeFlag;

    @Column(name = "open_camp_win_ids")
    private String openCampWinIds;

    @Column(name = "click_camp_win_ids")
    private String clickCampWinIds;

}
