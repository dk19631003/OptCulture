package com.optculture.launchpad.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.contact.SegmentRule;


public interface SegmentRuleRepository extends JpaRepository<SegmentRule, Long> {

	SegmentRule findBySegRuleId(Long id);

	List<SegmentRule> findBySegRuleIdIn(List<Long> segRuleIds);

}
