
package org.mq.marketer.campaign.general; 


import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.dao.ApplicationPropertiesDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PropertyUtil {

    private static Properties properties;
    private static PreparedStatement pstmt;
    private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public static Map<String, Short> emailCategoriesMap = new HashMap<String, Short>();
    public static Map<String, String> errorMessageMap = new HashMap<String, String>();     
    public static LRUCache<String, String> POSVersionCache = new LRUCache<String, String>(1000); 
    public static LRUCache<Long, Long> isOCAdminCache = new LRUCache<Long, Long>(1000);
    

    private static final Map<String, String> applicationPropsMap = new ConcurrentHashMap<>();//This ConcurrentHashMap is Threadsafe
    

    
    /**
     * Load application properties
     */
    static {
        
        properties = new Properties();
        try {
            URL url = PropertyUtil.class.getClassLoader().getResource(
                    "application.properties");

            logger.debug("loading properties file - url = " + url);
            properties.load(url.openStream());
            logger.debug("got Properties file ");
            
            getPreparedStmt();
            loadEmailCategories();
            
            
            
            
            /**
             * Load ErrorMessagefile properties
             */
            readErrorMessageFile( "*");
        	
            loadPOSVersionsCache(false, null, null);
            
            
            
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
    public static String getPOSVersion(String userName) {
        String value = null;

        try {
            
                value = POSVersionCache.get(userName);
               if(value !=null) value = value.trim();
               // logger.debug("getting the value of the property key : " + key+" value = "+value);
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
	public static synchronized String getPropertyValueFromDBOld(String key) {
		
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
	
	public static String getPropertyValueFromDB(String key) {


		try {

			logger.info("<<<<<<<<<<< Entered into PropertyUtil.getPropertyValueFromDB >>>>>>>>>>>>>");


			ApplicationPropertiesDao applicationPropertiesDao = (ApplicationPropertiesDao)(ServiceLocator.getInstance().getDAOByName("applicationPropertiesDao"));
			ApplicationProperties applicationProperties = null;

			if(applicationPropsMap.isEmpty()) {
				
				synchronized(applicationPropsMap) {//sothat another thread wont add props again
					
					if(applicationPropsMap.isEmpty()) {

						List<ApplicationProperties> applicationPropertiesList =	applicationPropertiesDao.findAll();
		
						Iterator<ApplicationProperties> itr = applicationPropertiesList.iterator();
		
						while(itr.hasNext()) {
		
							try {
							ApplicationProperties applicationProps= itr.next();
							logger.debug("adding into hashmap with key"+applicationProps.getKey());
							applicationPropsMap.put(applicationProps.getKey(), applicationProps.getValue());
							}catch(Exception e) {
								
								logger.error("** Exception : iterating over application properties list "+key, e);
								
							}
						}
						
					}
				}
			}
			
			logger.debug("applicationPropsMap size is ="+applicationPropsMap.size());
			
			// Check if propValue is already in the cachegetPropertyValue
			String propsValue = applicationPropsMap.get(key);
			if (propsValue != null) {
				logger.debug("Cache propsValue for key =" + key);//TODO keep only for testing
				return propsValue;
			}

			// If not in the cache, fetch from the db
			applicationProperties = applicationPropertiesDao.findByPropertyKey(key);

			if(applicationProperties!=null && applicationProperties.getValue()!=null)
				applicationPropsMap.put(applicationProperties.getKey(), applicationProperties.getValue());

			return applicationProperties.getValue();

		} catch (Exception e) {
			logger.error("** Exception : while gettting the value for key-"+key, e);
		}
		logger.debug("property value not found for key ="+key);
		return null;

	}//getPropertyValueFromDB

	
	
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
			logger.error("Exception ::" , e);
		}
	}
	
public static void loadPOSVersionsCache(boolean addOrUpdate, String userName, String POSVersion) {
		
		try {
			if(!addOrUpdate) {
				Connection conn = getConnection();
				Statement stmt = conn.createStatement(ResultSet.CONCUR_READ_ONLY,
						ResultSet.TYPE_SCROLL_SENSITIVE);
				String qryStr = 
					" SELECT username, POS_version FROM users WHERE enabled =1 AND POS_version IS NOT NULL" ;
				
				ResultSet rs = stmt.executeQuery(qryStr);
				
				while(rs.next()) {
					POSVersionCache.put(rs.getString("username"),rs.getString("POS_version"));
				}
			}else if(addOrUpdate && userName != null && POSVersion != null && !POSVersion.trim().isEmpty()){
				
				/*if(POSVersionCache.get(userName) != null) {//existing user POSversion has changed
					
				}else{//added a new account / POSversion 
					
					
				}*/
				POSVersionCache.put(userName,  POSVersion);
				
			}
		} catch (SQLException e) {
			logger.error("Exception ::" , e);
		}
	}
	
	/**
     * 
     * @param xmlFilePath
     * @param tag
     */
    private static void readErrorMessageFile(String tag) {
		
		try {
			
			URL url1 = PropertyUtil.class.getClassLoader().getResource(
                    "Messages.xml");
			String inputFileStr = url1.getFile();
			File inputFile = new File(inputFileStr);
			
//			File inputFile = new File("Messages.xml");
			if(!inputFile.exists()) {
				logger.debug("  Input file not found :"+inputFileStr);
			}
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document doc = db.parse(inputFile);
			
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName(tag);

			for (int i = 0; i < nodeLst.getLength(); i++) {
				Element fstNmElmnt = (Element) nodeLst.item(i);
				
				/*if(fstNmElmnt==null || !(fstNmElmnt.getNodeName().startsWith("M-") ||
						fstNmElmnt.getNodeName().startsWith("M-") ||
						fstNmElmnt.getNodeName().startsWith("M-") 
						)) {
					continue;
				}*/
				
				if(fstNmElmnt==null ) {
					continue;
				}
				
				errorMessageMap.put(fstNmElmnt.getNodeName(), fstNmElmnt.getAttribute("Text"));
			}
			
		} catch (Exception e) {
			logger.error("Exception :::",e);
		}
	} // readErrorMessageFile
    
    /**
     * Gets the value of the given key, reads from 'errorMessageFile' file
     * @param key
     * @return String value
     */
    public static String getErrorMessage(int errorNum,String moduleFlag) {
        String value = null;
        try {
        	String key = moduleFlag+"-"+errorNum;
            if (errorMessageMap.containsKey(key)) {
                value = errorMessageMap.get(key);
                value = value.trim();
            }
        } catch (Exception ex) {
            logger.error("** Exception " + ex.getMessage() + " **");
        }
        return value;
    }
  
    
    public static Long getisOCAdminCache(Long userId) {
        Long value = null;
        try {
                value = isOCAdminCache.get(userId);
               if(value !=null) { 
            	   return value;
               }
        } catch (Exception ex) {
            logger.error("** Exception in getOCAdminUserIdCache method" + ex.getMessage() + " **");
        }
		return null;
    }
}
