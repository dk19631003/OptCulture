package org.mq.captiway.scheduler.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClickHouseDBConnection {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private static Connection connection = null;

	public static Connection getClickHouseDBConnection() throws SQLException {
		logger.info(" -- Entered - getClickHouseDBConnection() -- ");

		if (connection != null) {
			return connection;
		} else {
			Properties prop = new Properties();
			prop.setProperty("username", "default");
			prop.setProperty("password", "5e5PNbQ8LGR.1");

			try {
				logger.info(" try-block ");
				Class.forName("com.clickhouse.jdbc.ClickHouseDriver");
				logger.info("try-block-2");
			} catch (ClassNotFoundException e) {
				logger.info("ClassNotFoundException Class.forName() ::");
				e.printStackTrace();
			}
			connection = DriverManager.getConnection("jdbc:ch:https://dmj4kd5cr7.us-east-2.aws.clickhouse.cloud:8443",prop);
			logger.info(" -- ClickHouse connection status :: " + ((connection != null) ? "Success" : "Failed"));
			logger.info(" -- Exit - getClickHouseDBConnection() -- ");
			return connection;
		}
	}

	public static ResultSet getClickHouseConnectionAndRunQuery(String query) {
		logger.info("1-CH query :: " + query);
		Statement stmt;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = ClickHouseDBConnection.getClickHouseDBConnection();
			stmt = conn.createStatement();
			logger.info("query string in clickhouse>>" + query);
			rs = stmt.executeQuery(query);
			logger.info("RESULTSET :: " + rs);
			logger.info("RESULTSET :: " + (rs == null));
		} catch (SQLException e) {
			logger.info("SQLException  Class.forName() :: ");
			e.printStackTrace();
		}
		return rs;
	}
}
