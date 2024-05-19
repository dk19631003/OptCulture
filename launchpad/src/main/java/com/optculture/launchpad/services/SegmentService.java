package com.optculture.launchpad.services;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.optculture.shared.segment.ClickHouseDB;
import com.optculture.shared.segment.SegmentQueryGenerator;

/* 
 * purpose : to get the respective segment query and get list of contacts from clickHouse DB

 */
@Service
public class SegmentService {

	@Value("${clickhouse.url}")
	private String hostUrl;

	@Value("${clickhouse.username}")
	private String username;

	@Value("${clickhouse.password}")
	private String password;
	
	Logger logger = LoggerFactory.getLogger(SegmentService.class);
	/*
	 * Purpose : Process rule and fetch query and get the contacts from respective query
	 * Pram : Segment Rule
	 * Return : list of contacts from clickhouse
	 */
	public List<Long> getSegmentContacts(List<String> segmentRuleList, String channelType) throws Exception {

		try {
			StringBuilder innerQuery = new StringBuilder("");
			String finalQuery = "SELECT DISTINCT any(cid) FROM contacts WHERE cid IN (<INNER-QUERIES>) ";
			for (String segRule : segmentRuleList) {
				String segmentQuery = "";
				segmentQuery = new SegmentQueryGenerator().getSegmentQuery(segRule, channelType);

				if (innerQuery.toString().isEmpty())
					innerQuery.append(segmentQuery);
				else
					innerQuery.append(" UNION DISTINCT " + segmentQuery);
			}

			finalQuery = finalQuery.replace("<INNER-QUERIES>", innerQuery);

			switch (channelType.toLowerCase()) {
			case "email":
				finalQuery += "GROUP BY email_id";
				break;

			case "sms", "whatsapp":
				finalQuery += "GROUP BY mobile_phone";
				break;

			default:
				break;
			}

		String[] dbConnectionInfo = { hostUrl, username, password };
		return new ClickHouseDB().getListOfContacts(dbConnectionInfo, finalQuery);
		}catch(Exception e) {
			logger.error("Exception while connecting : {}",e);
			throw new Exception(" Exception from click house :",e);
		}
	}
}
