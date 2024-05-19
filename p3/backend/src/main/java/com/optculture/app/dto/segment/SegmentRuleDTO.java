package com.optculture.app.dto.segment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.optculture.shared.entities.contact.SegmentRule;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SegmentRuleDTO {

	private Long segRuleId;
	private String segRuleName;
	private String description;
	private String segRule;
	private String type;
	private Long totSize;
	private Long totEmailSize;
	private Long totMobileSize;
	private LocalDateTime modifiedDate;
	private boolean isSelected =false;
	
	public SegmentRuleDTO(Long segRuleId, String segRuleName, String description, String type, Long totSize,
			Long totEmailSize, Long totMobileSize, LocalDateTime modifiedDate) {
		super();
		this.segRuleId = segRuleId;
		this.segRuleName = segRuleName;
		this.description = description;
		this.type = type;
		this.totSize = totSize;
		this.totEmailSize = totEmailSize;
		this.totMobileSize = totMobileSize;
		this.modifiedDate = modifiedDate;
	}

	public static List<SegmentRuleDTO> of(List<SegmentRule> listOfSegments) {
		ModelMapper modelMapper = new ModelMapper();
		List<SegmentRuleDTO> list = new ArrayList<>();
		for (SegmentRule segRuleObj : listOfSegments) {
			SegmentRuleDTO segObj = modelMapper.map(segRuleObj, SegmentRuleDTO.class);
			list.add(segObj);
		}
		return list;
	}
}
