

package org.mq.captiway.scheduler.utility; 
 

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PropertyUtil {

    private static Properties properties;
    private static PreparedStatement pstmt;
    private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
    public static Map<String, Short> emailCategoriesMap = new HashMap<String, Short>();
        
    /**
     * Load application properties
     */
    static {
        
        properties = new Properties();
        try {
            URL url = PropertyUtil.class.getClassLoader().getResource(
                    "application.properties");

            if(logger.isDebugEnabled()) logger.debug("loading properties file - url = " + url);
            properties.load(url.openStream());
            if(logger.isDebugEnabled()) logger.debug("got Properties file ");
            
            getPreparedStmt();
            loadEmailCategories();
        } catch (Exception ex) {
            logger.error("** Exception is " + ex.getMessage() +" **");
        }
    }
    
    /**
     * Gets the value of the given key, reads from 'application.properties' file
     * @param key
     * @return String value
     */
    public static String getPropertyValue(String key) {
        String value = null;

        try {
            
            if (properties.containsKey(key)) {
                value = properties.getProperty(key);
                value = value.trim();
               // logger.debug("getting the value of the property key : " + key+" value = "+value);
            }
        } catch (Exception ex) {
            logger.error("** Exception " + ex.getMessage() + " **");
        }
        return value;
    }
    
    private static Connection getConnection() {
    	
    	try {
            //Database connection
            Properties jdbcProps = new Properties();
            URL jdbcPropsUrl = PropertyUtil.class.getClassLoader().getResource("../jdbc.properties");
            jdbcProps.load(jdbcPropsUrl.openStream());
            
            Connection jdbcConn = DriverManager.getConnection(jdbcProps.getProperty("jdbc.url"), 
            		jdbcProps.getProperty("jdbc.username"), jdbcProps.getProperty("jdbc.password"));
           return jdbcConn;
           
        } catch (Exception ex) {
            logger.error("** Exception is ", ex);
            return null;
        }
    }
    
    /**
     * Prepares the PreparedStatement object by reinitializing the connection
     * @return
     */
    private static PreparedStatement getPreparedStmt() {

    	try {

    		Connection jdbcConn = getConnection();
            String qryStr = "SELECT props_value FROM application_properties WHERE props_key = ?";
            
            pstmt = jdbcConn.prepareStatement(qryStr);
        } catch (Exception ex) {
            logger.error("** Exception is ", ex);
        }
    	
    	return pstmt;
    	
    } // getPreparedStmt
    
    /**
     * Gets the value of the given key, gets from the 'application_properties' table(database).
     * @param key
     * @return
     */
	public static synchronized String getPropertyValueFromDB(String key) {
		
		try{
			ResultSet rs = null;
			
			try {
				pstmt.setString(1, key);
				rs = pstmt.executeQuery();
			}
			catch(Exception ex) {
				pstmt = getPreparedStmt();
				pstmt.setString(1, key);
				rs = pstmt.executeQuery();
			}
			
			if(rs != null && rs.first()) {
				return rs.getString("props_value");
			} 
			else {
				return null;
			}
			
		}catch(Exception e) {
			logger.error("** Exception : while gettting the value for key-"+key, e);
			return null;
		}
	}
	
	
	private static void loadEmailCategories() {
		
		try {
			
			Connection conn = getConnection();
			Statement stmt = conn.createStatement(ResultSet.CONCUR_READ_ONLY,
					ResultSet.TYPE_SCROLL_SENSITIVE);
			String qryStr = 
				" SELECT category_name, weightage FROM template_category WHERE weightage > 0" +
				" ORDER BY category_id";
			
			ResultSet rs = stmt.executeQuery(qryStr);
			
			while(rs.next()) {
				emailCategoriesMap.put(rs.getString("category_name"),rs.getShort("weightage"));
			}
		} catch (SQLException e) {
			logger.error("Exception ::::", e);
		}
	}
	
}
