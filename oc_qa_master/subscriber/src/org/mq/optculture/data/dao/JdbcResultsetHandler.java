package org.mq.optculture.data.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


/**
 * 
 */

/**
 * 
 * File : JdbcResultsetHandler.java
 * 
 * Description:
 * 		JdbcResultsetHandler is the util class which performs JDBC operations.
 * 
 * 
 * @author Manjunath Nunna
 * 
 * Created Date  :
 * 
 */
public class JdbcResultsetHandler {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
//	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//	private static String VF_DATA_SOURCE_JNDI = "ds/vodaFoneDatasource";
	protected static final short DEFAULT_ROWS_TO_RETURN = 1000; 

	private org.springframework.jdbc.datasource.DriverManagerDataSource dataSource;
	private Connection conn;
	protected ResultSet resultSet;
	protected ResultSetMetaData metadata;
	protected int columnCount;	
	private int totalCount=0;
	private  int currentFetchingCount;

	/*public long getTotalCount() {
		return totalCount;
	}*/
	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.info("Exception while rollback :::: ", e);
		}
	}
	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.info("Exception while rollback :::: ", e);
		}
	}
	
	public JdbcResultsetHandler() {
		init();
	}

	/**
	 * This is initialization method which establishes jdbc connection from datasource.
	 *
	 */
	public void init() {
		try {
			dataSource = (DriverManagerDataSource) ServiceLocator.getInstance().getBeanByName("dataSource");
			conn = dataSource.getConnection();
			logger.info("con is  ::"+conn);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Exception in JdbcResultsetHandler() constructor", e);
			destroy();
		} catch (Exception e) {
			logger.error("Exception in JdbcResultsetHandler() constructor", e);
			destroy();
		}
	}
	/**
	 * This method executes the DML query.
	 */
	
	/*public void executeUpdate(String sql) {

		try {
			PreparedStatement prpdStmt = conn.prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_READ_ONLY);
			
			
			prpdStmt.executeUpdate();

		} catch (SQLException e) {
			logger.error("Exception in executeStmt() method", e);
			destroy();
		}
	
	}*/
	/**
	 * This method executes the sql query.
	 * @param sql
	 */
	public void executeStmt(String sql) {
		
		executeStmt(sql, new Object[]{}, false);
	}
	
	
	/**
	 * This method executes the sql query. only for ExportTheard
	 * @param sql
	 */
	public void executeStmt(String sql,String exportThread) {
		
		executeStmt(sql, new Object[]{}, false,true);
	}
	
	
	
	
	
	/**
	 * This method executes the sql query and gives updatable Resultset.
	 * @param sql
	 * @param isUpdatableResultset
	 */
	public void executeStmt(String sql, boolean isUpdatableResultset)
	{
		executeStmt(sql, new Object[]{}, isUpdatableResultset);
	}
	/**
	 * This method will execute the sql query with parameter values
	 * @param sql
	 * @param params
	 */
	public void executeStmt(String sql, Object[] params, boolean isUpdatableResultset) {
		try {
			PreparedStatement prpdStmt = conn.prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE , isUpdatableResultset ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < params.length; i++) {
				prpdStmt.setObject(i + 1, params[i]);
			}
			//TODO check the fetch size
			// prpdStmt.getFetchSize();
			resultSet = prpdStmt.executeQuery();

			metadata = resultSet.getMetaData();
			columnCount = metadata.getColumnCount();
		
		} catch (SQLException e) {
			logger.error("Exception in executeStmt() method", e);
			destroy();
		}
	}
	
	
	
	
	//new by venkat
	
	public void executeStmt(String sql, Object[] params, boolean isUpdatableResultset,boolean isBulkFetch) {
		try {
			PreparedStatement prpdStmt = conn.prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE , isUpdatableResultset ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < params.length; i++) {
				prpdStmt.setObject(i + 1, params[i]);
			}
			//TODO check the fetch size
			if(isBulkFetch)	prpdStmt.setFetchSize(1000);
			resultSet = prpdStmt.executeQuery();
			metadata = resultSet.getMetaData();
			columnCount = metadata.getColumnCount();
		} catch (SQLException e) {
			logger.error("Exception in executeStmt() method", e);
			destroy();
		}
	}
	

	public int totalRecordsSize(){
		try{
			if(resultSet != null){
				resultSet.last();
				totalCount = resultSet.getRow();
				logger.info("totalCount---->"+totalCount);
				
				resultSet.beforeFirst();
			}
		}catch(Exception e){
			logger.error("Exception in totalRecordsSize() method", e);
		}
		return totalCount;
	}

	/**
	 * This method will fetch records from resultset
	 * @return List<String>
	 */
	public List<String> getRecords() {
		return getRecords(DEFAULT_ROWS_TO_RETURN); 
	}
	/**
	 * This method will fetch records from resultset. The return values will key=value separated by ";"
	 * @param rowsToReturn
	 * @return List<String>
	 */
	public List<String> getRecords(int rowsToReturn) {
		List<String> aList = null;
		try {
			if (resultSet != null && !resultSet.isAfterLast()) {
				aList = new ArrayList<String>();
				int rowsRead = 0;
				while (rowsRead < rowsToReturn && resultSet.next()) {
					StringBuilder keyValues = new StringBuilder();
					for (int column = 1; column <= columnCount; column++) {
						keyValues.append(metadata.getColumnName(column).toLowerCase()).
						append("=").append(resultSet.getString(column)).
						append(";").toString();
					}
					aList.add(keyValues.toString());
					rowsRead++;
				}
			} else {
				destroy();
			}
		} catch (SQLException e) {
			logger.error("Exception in getRecords() method", e);
			destroy();
		}
		if(aList != null) setCurrentFetchingCount(aList.size()+getCurrentFetchingCount());
		return aList;
	}

	public ResultSet getResultSet(){
		return resultSet;
	}
	
	/**
	 * This method will set the record list passed to the Map.
	 * @param recordsList
	 * @param configMap
	 */
	/*public void setRecordsListToMap(List<String> recordsList, Map<String, String> configMap){
		for (String recordString : recordsList) {
			StringTokenizer strTokenizer = new StringTokenizer(recordString, ";");
			while(strTokenizer.hasMoreElements()){
				String keyValue = (String) strTokenizer.nextElement();
				String[] keyValueArray = keyValue.split("=");
				configMap.put(keyValueArray[0], keyValueArray[1]);
			}
		}
	}*/
	/**
	 * This method will close the connection object created during initialization.
	 *
	 */
	public void destroy() {
		try {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
				resultSet = null;
			}
		} catch (SQLException e) {
			logger.error("Exception in destroy() method", e);
		}

		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			logger.error("Exception in destroy() method", e);
		}
	}
	/**
	 * This method convert the clob to string.
	 * @param clobObject
	 * @return
	 * @throws UPSSBaseException
	 */
	public static String clobToString(Clob clobObject) throws BaseServiceException{
		StringBuffer strBuffer = new StringBuffer();
		String str = "";
		try{
			BufferedReader br = new BufferedReader(clobObject.getCharacterStream());
			while ((str=br.readLine())!=null)
				strBuffer.append(str);
		}catch(SQLException sqlE){
			throw new BaseServiceException("Unable to get the Character Stream from clob object ::: ", sqlE);
		}catch(IOException e){
			throw new BaseServiceException("Unable to read from the characters from buffer ::: ", e);
		}
		return strBuffer.toString();
	}


	public int getCurrentFetchingCount() {
		return currentFetchingCount;
	}


	public void setCurrentFetchingCount(int currentFetchingCount) {
		this.currentFetchingCount = currentFetchingCount;
	}	
}
