package com.optculture.app.services;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.segment.SegmentRuleDTO;
import com.optculture.app.repositories.LoyaltyProgramTierRepository;
import com.optculture.app.repositories.LoyaltyProgramsRepository;
import com.optculture.app.repositories.SegmentRuleRepository;
import com.optculture.shared.entities.contact.SegmentRule;
import com.optculture.shared.entities.loyalty.LoyaltyProgram;
import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;
import com.optculture.shared.segment.ClickHouseDB;
import com.optculture.shared.segment.SegmentQueryGenerator;

@Service
public class SegmentRuleService {

    @Autowired
    private SegmentRuleRepository segmentRuleRepository;

    @Autowired
    private GetLoggedInUser getLoggedInUser;

	@Autowired
	private LoyaltyProgramsRepository loyaltyProgramsRepository;

	@Autowired
	private LoyaltyProgramTierRepository loyaltyProgramTierRepository;

	@Value("${clickhouse.url}")
	private String hostUrl;

	@Value("${clickhouse.username}")
	private String username;

	@Value("${clickhouse.password}")
	private String password;

	private final Logger logger = LoggerFactory.getLogger(SegmentRuleService.class);

    public List<SegmentRuleDTO> getListOfSegmentRules() {
        List<SegmentRule> segmentList = segmentRuleRepository.findByUserIdOrderByModifiedDateDesc(getLoggedInUser.getLoggedInUser().getUserId());
        return SegmentRuleDTO.of(segmentList);
    }

    public Page<SegmentRuleDTO> findByUserIdAndSegRuleName(Long userId, String name, int pageNumber) {
        PageRequest pg=PageRequest.of(pageNumber,150, Sort.by(Sort.Direction.DESC,"segRuleId"));
        SegmentRule segmentRule= new SegmentRule();
        segmentRule.setUserId(userId);
        if(!name.equalsIgnoreCase("--"))
        segmentRule.setSegRuleName(name);
        return segmentRuleRepository.findByExample(segmentRule,pg);
    }
    public List<SegmentRuleDTO> getSelectedSegmentByIds(String segmentIdString, Long userId) {
        List<Long> segmentIdList= Arrays.asList(segmentIdString.split(",")).stream().mapToLong(Long::parseLong).boxed().toList();
        List<SegmentRuleDTO> segmentRuleDtoList=segmentRuleRepository.findBySegmentRuleIds(segmentIdList,userId);
        return segmentRuleDtoList;
    }
    
	public ResponseEntity<String> saveOrUpdateSegment(Map<String, Object> segmentData) {

		SegmentRule segRuleObj = getSegmentQueryAndCounts(segmentData.get("segmentRule").toString());
		
		Long segRuleId = null;
		try {
			segRuleId = Long.parseLong(segmentData.get("segRuleId").toString());
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException");
		}

		SegmentRule existingSegRule = null;
		if (segRuleId != null && segRuleId > 0) {
			existingSegRule = segmentRuleRepository.findOneBySegRuleIdAndUserId(segRuleId, segRuleObj.getUserId());
		}

		if (!Objects.isNull(existingSegRule)) {
			logger.info("EXISTING - segment update");
			existingSegRule.setDescription(Objects.isNull(segmentData.get("description")) ? "" : segmentData.get("description").toString());
			existingSegRule.setModifiedDate(LocalDateTime.now());
			existingSegRule.setSegRule(segRuleObj.getSegRule());
			existingSegRule.setTotSize(segRuleObj.getTotSize());
			existingSegRule.setTotEmailSize(segRuleObj.getTotEmailSize());
			existingSegRule.setTotMobileSize(segRuleObj.getTotMobileSize());
			existingSegRule.setTotSegQuery(segRuleObj.getTotSegQuery());
			existingSegRule.setEmailSegQuery(segRuleObj.getEmailSegQuery());
			existingSegRule.setMobileSegQuery(segRuleObj.getMobileSegQuery());

			segmentRuleRepository.save(existingSegRule);
			return ResponseEntity.status(HttpStatus.OK).body("Segment Updated");
		} else {
			logger.info("NEW - segment save");
			segRuleObj.setSegRuleName(segmentData.get("segmentName").toString());
			segRuleObj.setDescription(Objects.isNull(segmentData.get("description")) ? "" : segmentData.get("description").toString());
			segRuleObj.setCreatedDate(LocalDateTime.now());
			segRuleObj.setModifiedDate(LocalDateTime.now());

			segmentRuleRepository.save(segRuleObj);
			return ResponseEntity.status(HttpStatus.OK).body("Segment Saved");
		}
	}

	public SegmentRule getSegmentQueryAndCounts(String segmentRule) {

		SegmentRule segRuleObj = new SegmentRule();
		long userId = new GetLoggedInUser().getLoggedInUser().getUserId();
		segmentRule = "all:" + userId + "||" + segmentRule;
		String countQuery = "SELECT COUNT(*) FROM (<QUERY>)";

		try {
			String allContactsQry = new SegmentQueryGenerator().getSegmentQuery(segmentRule, "ALL_CONTACTS");
			String activeEmailContactsQry = new SegmentQueryGenerator().getSegmentQuery(segmentRule, "ACTIVE_EMAIL_CONTACTS");
			String activeMobileContactsQry = new SegmentQueryGenerator().getSegmentQuery(segmentRule, "ACTIVE_MOBILE_CONTACTS");

			String[] dbConnectionInfo = { hostUrl, username, password };

			ClickHouseDB chObj = new ClickHouseDB();

			try (Connection connection = chObj.getClickHouseDBConnection(dbConnectionInfo)) {

				long totalSize = chObj.getClickHouseCount(connection, countQuery.replace("<QUERY>", allContactsQry));
				long activeEmailCount = chObj.getClickHouseCount(connection, countQuery.replace("<QUERY>", activeEmailContactsQry));
				long activeMobileCount = chObj.getClickHouseCount(connection, countQuery.replace("<QUERY>", activeMobileContactsQry));

				segRuleObj.setSegRule(segmentRule);
				segRuleObj.setUserId(userId);
				segRuleObj.setTotSize(totalSize);
				segRuleObj.setTotEmailSize(activeEmailCount);
				segRuleObj.setTotMobileSize(activeMobileCount);
				segRuleObj.setTotSegQuery(allContactsQry);
				segRuleObj.setEmailSegQuery(activeEmailContactsQry);
				segRuleObj.setMobileSegQuery(activeMobileContactsQry);

				logger.info("SEG-RULE :: {}", segmentRule);
				logger.info("ALL_CONTACTS :: {}", totalSize);
				logger.info("Email_CONTACTS :: {}", activeEmailCount);
				logger.info("Mobile_CONTACTS :: {}", activeMobileCount);
			}
		} catch (Exception e) {
			logger.error("EXCEPTION in getSegmentData() method", e);
		}

		return segRuleObj;
	}

	public Map<String, String> getActiveLoyaltyProgramTiers() {
		Map<String, String> ltyPrgTiersMap = new LinkedHashMap<>();
		List<Long> ltyPrgIds = new ArrayList<>();

		List<LoyaltyProgram> ltyPrgsList = loyaltyProgramsRepository.findByUserIdAndStatusIn(getLoggedInUser.getLoggedInUser().getUserId(), Arrays.asList("Active", "Suspended"));

		ltyPrgsList.forEach(ltyPrg -> ltyPrgIds.add(ltyPrg.getProgramId()));
		List<LoyaltyProgramTier> ltyPrgTiersList = loyaltyProgramTierRepository.findAllByProgramIdIn(ltyPrgIds);

		for (LoyaltyProgram ltyPrg : ltyPrgsList) {
			for (LoyaltyProgramTier ltyPrgTier : ltyPrgTiersList) {
				if (ltyPrg.getProgramId().equals(ltyPrgTier.getProgramId())) {
					ltyPrgTiersMap.put(ltyPrg.getProgramName() + " : " + ltyPrgTier.getTierType(), ltyPrgTier.getTierId().toString());
				}
			}
		}
		return ltyPrgTiersMap;
	}

}
