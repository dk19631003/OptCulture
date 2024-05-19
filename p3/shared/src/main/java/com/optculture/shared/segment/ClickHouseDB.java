package com.optculture.shared.segment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClickHouseDB {

	private static final Logger logger = LoggerFactory.getLogger(ClickHouseDB.class);

	private Connection conn = null;

	public Connection getClickHouseDBConnection(String[] dbConnectionInfo) throws SQLException {
		Properties prop = new Properties();
		prop.setProperty("username", dbConnectionInfo[1]);
		prop.setProperty("password", dbConnectionInfo[2]);
		prop.put("socket_timeout", "600000");
		int attempts = 0;
		
		while (Objects.isNull(conn) || conn.isClosed()) {
			try {
				conn = DriverManager.getConnection(dbConnectionInfo[0], prop);
				logger.info("Clickhouse Get-Connection attempt : {} - {}", ++attempts, "Success ");
			} catch (Exception e) {
				logger.info("Clickhouse Get-Connection attempt : {} - {}", ++attempts, "Failed ");
			}
			if ((!Objects.isNull(conn) && !conn.isClosed()) || attempts >= 3)
				break;
		}

		return conn;

	}

	public List<Long> getListOfContacts(String[] dbConnectionInfo, String query) throws Exception {
		List<Long> contactList = new ArrayList<>();
		ResultSet rs = null;
		try {
			Connection connection = getClickHouseDBConnection(dbConnectionInfo);
			logger.info(" -- Got ClickHouse Connection -- ");
			try (Statement stmt = connection.createStatement()) {
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					contactList.add(rs.getLong(1));
				}
			}
			logger.info("Segment Query Executed, Got : {} contacts", contactList.size());
		} catch (SQLException e) {
			logger.error("ClickHouse Exception :: ", e);
			throw new Exception("An error occurred in clickhouse" + e);
		} catch (Exception e) {
			logger.error("ClickHouse Exception :: ", e);
			throw new Exception("An error occurred in generic Method" + e);
		}
		return contactList;
	}

	public long getClickHouseCount(Connection conn, String query) {
		ResultSet resultSet = null;
		long count = 0;
		try (Statement stmt = conn.createStatement()) {
			resultSet = stmt.executeQuery(query);

			if (resultSet.next()) {
				count = resultSet.getLong(1);
			}
		} catch (Exception e) {
			logger.error("Exception while running Segment-Query :: ", e);
		}
		return count;
	}
}
