package com.optculture.shared.entities.org;

import jakarta.persistence.*;
@Entity
@Table(name = "userdomain")
public class UsersDomains {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "domain_id")
    private java.lang.Long domainId;

    @Column(name = "external_id", length = 20)
    private String externalId;

    @Column(name = "domain_name", length = 64)
    private String domainName;

    @Column(name = "display_name", length = 64)
    private String displayName;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_managerid")
    private User domainManagerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_organization")
    private UserOrganization userOrganization;

/*
TODO: Convert Set
<set name="mailingLists" table="mlists_domains">
			<key column="domain_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.MailingList" column="list_id" />
		</set>
*/
/*
TODO: Convert Set
<set name="segments" table="segments_domains">
			<key column="domain_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.SegmentRules" column="seg_rule_id" />
		</set>
*/
}
