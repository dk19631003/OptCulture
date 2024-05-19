package com.optculture.shared.entities.org;

import jakarta.persistence.*;
@Entity
@Table(name = "org_zones")
public class OrganizationZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private java.lang.Long zoneId;

    @Column(name = "zone_name", length = 100)
    private String zoneName;

    @Column(name = "description", length = 50)
    private String Description;

    @Column(name = "created_date", length = 50)
    private java.util.Calendar createdDate;

    @Column(name = "modified_date", length = 50)
    private java.util.Calendar modifiedDate;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "domain_id", length = 50)
    private long domainId;

    @Column(name = "delete_status")
    private boolean deleteStatus;

/*
TODO: Convert Set
<set name="stores" table="zone_store" lazy="false" cascade="save-update" fetch="select">
			<key column="zone_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.OrganizationStores" column="store_id" />
		</set>
*/
}
