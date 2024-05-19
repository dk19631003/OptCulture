package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "sec_groups")
public class SecGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private java.lang.Long group_id;

    @Column( length = 100)
    private String name;

    @Column( length = 255)
    private String description;

    @Column( length = 50)
    private String version;

    @Column( length = 50)
    private String type;

/*
TODO: Convert Set
<set name="rightsSet" table="sec_groups_rights">
			<key column="group_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.SecRights" column="right_id" />
		</set>
*/
}
