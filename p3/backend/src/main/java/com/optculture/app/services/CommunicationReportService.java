package com.optculture.app.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.campaign.CampaignReport;
import com.optculture.app.dto.campaign.HourlyEventCountsDTO;
import com.optculture.app.repositories.CommunicationReportRepository;
import com.optculture.app.repositories.ScheduleRepository;
import com.optculture.app.repositories.SegmentRuleRepository;
import com.optculture.shared.entities.communication.CommunicationEvent;
import com.optculture.shared.entities.communication.CommunicationReport;
import com.optculture.shared.entities.contact.SegmentRule;
import com.optculture.shared.segment.ClickHouseDB;

@Service
public class CommunicationReportService {
	
	private static Logger logger = LoggerFactory.getLogger(CommunicationReportService.class);
	
	@Value("${clickhouse.url}")
	private String hostUrl;

	@Value("${clickhouse.username}")
	private String username;

	@Value("${clickhouse.password}")
	private String password;
	
	@Autowired
	private GetLoggedInUser getLoggedInUser;

	@Autowired
	private CommunicationReportRepository communicationReportRepository;

	@Autowired
	private SegmentRuleRepository segmentRuleRepository;
	
	@Autowired
	private ScheduleRepository scheduleRepository;

	public CampaignReport getCampaignReport(long crId) {
		Long userId = getLoggedInUser.getLoggedInUser().getUserId();


		CommunicationReport communicationReport = communicationReportRepository.findOneByCrId(crId);
		String segmentIds = scheduleRepository.findOneByUserIdAndCrId(userId, crId).getCommunication().getSegmentId().split(":")[1];

		
		List<Long> segmentIdList = Arrays.stream(segmentIds.split(",")).map(Long::parseLong).toList();
		String[] segmentNames = segmentRuleRepository.findByUserIdAndSegRuleIdIn(userId, segmentIdList).stream().map(SegmentRule::getSegRuleName).toArray(String[]::new);

		CampaignReport campaignReport = new CampaignReport();
		campaignReport.setCampaignName(communicationReport.getName());
		campaignReport.setCampaignSentDate(communicationReport.getSentDate());
		campaignReport.setTotalCount(communicationReport.getConfigured());
		campaignReport.setCommunicationType(communicationReport.getSourceType());
		campaignReport.setSentTemplate(communicationReport.getContent());
		campaignReport.setSegmentNames(segmentNames);
		
		if (campaignReport.getCommunicationType().toUpperCase().contains("EMAIL")) campaignReport.setCommunicationType("Email Campaign");
		else if (campaignReport.getCommunicationType().toUpperCase().contains("SMS")) campaignReport.setCommunicationType("SMS Campaign");
		else if (campaignReport.getCommunicationType().toUpperCase().contains("WHATSAPP")) campaignReport.setCommunicationType("WhatsApp Campaign");

		

		ClickHouseDB clickHouseDb = new ClickHouseDB();
		String[] dbConnectionInfo = { hostUrl, username, password };

		try (Connection connection = clickHouseDb.getClickHouseDBConnection(dbConnectionInfo);
				Statement statement = connection.createStatement();) {

			Map<String, String> eventCountsMap = new LinkedHashMap<>();

			String eventCountsQuery = "SELECT event_type, COUNT(*) AS counts  FROM communication_event WHERE cr_id = " + crId + " GROUP BY event_type ORDER BY counts DESC";

			ResultSet eventCountsRS = statement.executeQuery(eventCountsQuery);
			while (eventCountsRS.next()) {
				eventCountsMap.put(eventCountsRS.getString(1), eventCountsRS.getString(2));
			}
			campaignReport.setEventCountMap(eventCountsMap);

			String coutsPerHourQuery = "SELECT toStartOfHour(event_date) AS event_hour <SELECT_EVENT_COUNTS> FROM communication_event WHERE cr_id = " + crId + " GROUP BY event_hour ORDER BY event_hour LIMIT 9";

			List<String> listOfEvents = new ArrayList<>(eventCountsMap.keySet());

			StringBuilder eventCountsColumns = new StringBuilder("");
			listOfEvents.forEach(eventType -> eventCountsColumns.append(" , countIf(event_type= '" + eventType + "') AS " + eventType.replaceAll("\\s", "")));

			coutsPerHourQuery = coutsPerHourQuery.replace("<SELECT_EVENT_COUNTS>", eventCountsColumns.toString());

			ResultSet eventCountsPerHourRS = statement.executeQuery(coutsPerHourQuery);
			int columnCount = eventCountsPerHourRS.getMetaData().getColumnCount();

			List<HourlyEventCountsDTO> listOfHourlyEvents = new ArrayList<>();
			long[][] eventCountsArray = new long[9][columnCount - 1];

			for (int row = 0; row < 9; row++) {
				if (eventCountsPerHourRS.next()) {
					for (int col = 0; col < columnCount - 1; col++) {
						eventCountsArray[row][col] = eventCountsPerHourRS.getLong(col + 2);
					}
				} else {
					for (int col = 0; col < columnCount - 1; col++) {
						eventCountsArray[row][col] = 0;
					}
				}
			}

			for (int col = 0; col < columnCount - 1; col++) {

				long[] array = new long[9];

				for (int row = 0; row < 9; row++) {
					array[row] = eventCountsArray[row][col];
				}

				HourlyEventCountsDTO hourlyEventCountsObj = new HourlyEventCountsDTO();
				hourlyEventCountsObj.setName(listOfEvents.get(col));
				hourlyEventCountsObj.setData(array);

				listOfHourlyEvents.add(hourlyEventCountsObj);

			}
			campaignReport.setListOfhourlyEventCounts(listOfHourlyEvents);

		} catch (SQLException e) {
			logger.error("Exception :: ", e);
		}

		return campaignReport;

	}


	public List<CommunicationEvent> getCampaignRecipientEventsList(long crId, String recipientCond, String statusCond) {
		
		String query = "SELECT recipient, event_type, event_date FROM communication_event ce WHERE cr_id = " + crId + recipientCond + statusCond + " ORDER BY event_date DESC, recipient ASC LIMIT 1000";
		String[] dbConnectionInfo = { hostUrl, username, password };
		List<CommunicationEvent> recipientEventsList = new ArrayList<>();
		
		try (Connection conn = new ClickHouseDB().getClickHouseDBConnection(dbConnectionInfo);
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(query);) {
			while(rs.next()) {
				CommunicationEvent event = new CommunicationEvent(rs.getString("recipient"), rs.getString("event_type"), rs.getTimestamp("event_date").toLocalDateTime());
				recipientEventsList.add(event);
			}

		} catch (SQLException e) {
			logger.error("Exception :: ", e);
		}

		return recipientEventsList;
	}
}
