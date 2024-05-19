package com.optculture.shared.entities.contact;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Table(name = "segment_rules")
@Data
public class SegmentRule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seg_rule_id")
	private Long segRuleId;

	@Column(name = "seg_rule_name")
	private String segRuleName;

	@Column(name = "description")
	private String description;

	@Column(name = "ml_list_id")
	private Long listId;

	@Column(name = "last_refreshed_on")
	private LocalDateTime lastRefreshedOn;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@Column(name = "size")
	private Long totEmailSize;

	@Column(name = "tot_size")
	private Long totSize;

	@Column(name = "tot_mobile_size")
	private Long totMobileSize;

	@Column(name = "seg_rule")
	private String segRule;

	@Column(name = "seg_type")
	private String segmentType;

	@Column(name = "segment_mlist_ids", length = 500)
	private String segmentMlistIdsStr;

	@Column(name = "seg_query")
	private String emailSegQuery;

	@Column(name = "mobile_seg_query")
	private String mobileSegQuery;

	@Column(name = "tot_seg_query")
	private String totSegQuery;

	@Column(name = "seg_rule_to_view")
	private String segRuleToView;

	@Column(name = "user_id")
	private Long userId;

	/*
	 * TODO: Convert Set <set name="sharedToDomain" table="segments_domains"> <key
	 * column="seg_rule_id" /> <many-to-many
	 * class="org.mq.marketer.campaign.beans.UsersDomains" column="domain_id" />
	 * </set>
	 */
}
