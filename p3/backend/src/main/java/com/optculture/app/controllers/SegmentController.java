package com.optculture.app.controllers;

import java.util.List;
import java.util.Map;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.shared.entities.org.User;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.optculture.app.dto.segment.SegmentRuleDTO;
import com.optculture.app.services.CommunicationService;
import com.optculture.app.services.CouponsService;
import com.optculture.app.services.PosMappingService;
import com.optculture.app.services.SegmentRuleService;
import com.optculture.shared.segment.*;

@RestController
@CrossOrigin
@RequestMapping("/api/segment")
public class SegmentController {

	@Value("${clickhouse.url}")
	private String hostUrl;

	@Value("${clickhouse.username}")
	private String username;

	@Value("${clickhouse.password}")
	private String password;
	
	@Autowired
	private SegmentRuleService segmentRuleService;

	@Autowired
	private GetLoggedInUser getLoggedInUser;
	
	@Autowired
	private CommunicationService communicationService;
	
	@Autowired
	private CouponsService couponsService;
	
	@Autowired
	private PosMappingService posMappingService;

	@GetMapping("/{segmentRule}")
	public List<Long> getSegmentContacts(@PathVariable(name = "segmentRule") String segmentRule) throws Exception {
		try{
		String finalQuery = new SegmentQueryGenerator().getSegmentQuery(segmentRule, "ALL_CONTACTS"); //ALL_CONTACTS, ACTIVE_EMAIL_CONTACTS, ACTIVE_MOBILE_CONTACTS           
		String[] dbConnectionInfo = { hostUrl, username, password };

		return new ClickHouseDB().getListOfContacts(dbConnectionInfo, finalQuery);
	}
	catch(Exception e){
		throw new Exception("Exception while connecting Clickhouse ", e);
	}
	}

	@GetMapping("/fetch-all")
	public List<SegmentRuleDTO> getListOfSegmentRules(){
		return segmentRuleService.getListOfSegmentRules();
	}

	@GetMapping("/segment-list")
	public ResponseEntity getSegmentList(@RequestParam int pageNumber, @RequestParam(defaultValue = "--",required = false) String segmentName){
		User user=getLoggedInUser.getLoggedInUser();
		Page<SegmentRuleDTO> segmentDtos= segmentRuleService.findByUserIdAndSegRuleName(user.getUserId(),segmentName,pageNumber);
		return new ResponseEntity<>(segmentDtos, HttpStatus.OK);
	}
	@GetMapping("/selected")
	public ResponseEntity	getSelectedSegmentByIds(@RequestParam String segmentIds){
		User user=getLoggedInUser.getLoggedInUser();
		return new ResponseEntity(segmentRuleService.getSelectedSegmentByIds(segmentIds,user.getUserId()),HttpStatus.OK);
	}

	@PostMapping("/get-segment-data")
	public SegmentRuleDTO getSegmentData(@RequestBody Map<String, Object> segmentData) {
		return new ModelMapper().map(segmentRuleService.getSegmentQueryAndCounts(segmentData.get("segmentRule").toString()), SegmentRuleDTO.class);
	}

	@PostMapping("/save-segment")
	public ResponseEntity<String> saveSegment(@RequestBody Map<String, Object> segmentData) {
		return segmentRuleService.saveOrUpdateSegment(segmentData);
	}

	@GetMapping("/coupon-names")
	public Map<String, String> fetchActiveAndRunningCouponNames() {
		return couponsService.getActiveAndRunningCouponsMap();
	}

	@GetMapping("/campaign-names")
	public Map<String, String> fetchAllCampaignNames() {
		return communicationService.getAllCampaignNames();
	}
	
	@GetMapping("/lty-prg-tiers")
	public Map<String, String> fetchLoyaltyProgramTiers() {
		return segmentRuleService.getActiveLoyaltyProgramTiers();
	}

	@GetMapping("/udfs")
	public List<Map<String, String>> getMappedUdfs() {
		Long userId = getLoggedInUser.getLoggedInUser().getUserId();
		return posMappingService.getPosMappedUdfs(userId);
	}

}
