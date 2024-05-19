package com.optculture.shared.entities.config;

import java.util.Set;

import jakarta.persistence.*;
@Entity
@Table(name = "sec_roles")
public class SecRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private java.lang.Long role_id;

    @Column( length = 100)
    private String name;

    @Column( length = 255)
    private String description;

    @Column( length = 50)
    private String version;

    @Column( length = 50)
    private String type;

    @ManyToMany
    @JoinTable(
        name = "sec_roles_groups",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )

    private Set<com.optculture.shared.entities.config.SecGroups> groups;


/*
TODO: Convert Set
<set name="groupsSet" table="sec_roles_groups">
			<key column="role_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.SecGroups" column="group_id" />
		</set>
*/
}
