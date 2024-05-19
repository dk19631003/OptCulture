package org.mq.marketer.campaign.general;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClickHouseDB {

	private static final Logger logger = LoggerFactory.getLogger(ClickHouseDB.class);

	private Connection getClickHouseDBConnection() throws SQLException {
		Properties prop = new Properties();
		prop.setProperty("username", "default");
		prop.setProperty("password", "5e5PNbQ8LGR.1");

		try {
			Class.forName("com.clickhouse.jdbc.ClickHouseDriver");
		} catch (ClassNotFoundException e) {
			logger.info("ClassNotFoundException Class.forName() ::"+ e);
		}
		return DriverManager.getConnection("jdbc:ch:https://dmj4kd5cr7.us-east-2.aws.clickhouse.cloud:8443", prop);
	}

	public List<Long> getListOfContacts(String query) {
		List<Long> contactList = new ArrayList<>();
		ResultSet rs = null;
		try (Connection conn = getClickHouseDBConnection()) {
			logger.info(" -- Got ClickHouse Connection -- ");
			try (Statement stmt = conn.createStatement()) {
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					contactList.add(rs.getLong(1));
				}
			}
			logger.info("Segment Query Executed, Got : {} contacts", contactList.size());
		} catch (SQLException e) {
			logger.error("ClickHouse Exception :: ", e);
		}
		return contactList;
	}

	public int getCHConnectionAndRunCountQuery(String query) {
		int count = 0;
		ResultSet resultSet = null;
		try (Connection conn = getClickHouseDBConnection()) {
			try (Statement stmt = conn.createStatement()) {
				resultSet = stmt.executeQuery(query);
				if (resultSet.next()) {
					count = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.error("ClickHouse Exception :: ", e);
		}
		return count;
	}
}
