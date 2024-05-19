package org.mq.optculture.utils;


import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;

import au.com.bytecode.opencsv.ResultSetHelperService;
/**
 * 
 * @author proumyaa
 *unless we have some customizations , no specific methods are required in this class.<br>
 *Example : other than any specific date formats,field seperators, enclosing characters.
 */
public class OCResultSetHelperService extends ResultSetHelperService
{
  private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public OCResultSetHelperService(){
		super();
	}
	private void getColumns(ArrayList localArrayList, ResultSet paramResultSet, int paramInt1, int paramInt2) throws SQLException, IOException
	{
		String str = paramResultSet.getString(paramInt2);
				
		String[] columns = str.split(";::;");
		for(String col:columns)
		{
			
			localArrayList.add(""+col+"");
		}
	}
	
	public String[] getColumnValues(ResultSet paramResultSet, int indexToIgnore, int multipleValuesColumn)
		    throws SQLException, IOException
		  {
		    ArrayList localArrayList = new ArrayList();
		    ResultSetMetaData localResultSetMetaData = paramResultSet.getMetaData();
		    for (int i = 1; i <= localResultSetMetaData.getColumnCount(); i++) {
		    	if(indexToIgnore == i)continue;
		    	if(multipleValuesColumn == i){
		    		getColumns(localArrayList, paramResultSet, localResultSetMetaData.getColumnType(i ), i );
		    	}
		    	else
		    	{
		      localArrayList.add(getColumnValue(paramResultSet, localResultSetMetaData.getColumnType(i ), i ));
		    	}
		    }
		    String[] arrayOfString = new String[localArrayList.size()];
		    return (String[])localArrayList.toArray(arrayOfString);
		  }
	
	
	@SuppressWarnings("unchecked")
	public String[] getColumnValues(ResultSet paramResultSet, int indexToIgnore)
		    throws SQLException, IOException
		  {
		    ArrayList localArrayList = new ArrayList();
		    ResultSetMetaData localResultSetMetaData = paramResultSet.getMetaData();
		    for (int i = 0; i < localResultSetMetaData.getColumnCount(); i++) {
		    	if(indexToIgnore == i+1)continue;
		      localArrayList.add(getColumnValue(paramResultSet, localResultSetMetaData.getColumnType(i + 1), i + 1));
		    }
		    String[] arrayOfString = new String[localArrayList.size()];
		    return (String[])localArrayList.toArray(arrayOfString);
		  }
	public String[] getColumnValues(ResultSet paramResultSet, int[] indexToIgnore)
		    throws SQLException, IOException
		  {
		    ArrayList localArrayList = new ArrayList();
		    ResultSetMetaData localResultSetMetaData = paramResultSet.getMetaData();
		    for (int i = 0; i < localResultSetMetaData.getColumnCount(); i++) {
		    	boolean ignoreColumn = false;
		    	for (int index : indexToIgnore) {
		    		if(index == i+1) {
		    			ignoreColumn = true;
		    			break;
		    		}
				}
		    	if(ignoreColumn) continue;
		      localArrayList.add(getColumnValue(paramResultSet, localResultSetMetaData.getColumnType(i + 1), i + 1));
		    }
		    String[] arrayOfString = new String[localArrayList.size()];
		    return (String[])localArrayList.toArray(arrayOfString);
		  }
	
	  private String getColumnValue(ResultSet paramResultSet, int paramInt1, int paramInt2)
			    throws SQLException, IOException
			  {
			    String str = "";
			    switch (paramInt1)
			    {
			    case -7: 
			    case 2000: 
			      str = handleObject(paramResultSet.getObject(paramInt2));
			      break;
			    case 16: 
			      boolean bool = paramResultSet.getBoolean(paramInt2);
			      str = Boolean.valueOf(bool).toString();
			      break;
			    case 2005: 
			    case 2011: 
			      Clob localClob = paramResultSet.getClob(paramInt2);
			      if (localClob != null) {
			        str = read(localClob);
			      }
			      break;
			    case -5: 
			      str = handleLong(paramResultSet, paramInt2);
			      break;
			    case 2: 
			    case 3: 
			    case 6: 
			    case 7: 
			    case 8: 
		//	      str = handleBigDecimal(paramResultSet.getBigDecimal(paramInt2));
			    	str = handleDouble(paramResultSet, paramInt2);
			      break;
			    case -6: 
			    case 4: 
			    case 5: 
			      str = handleInteger(paramResultSet, paramInt2);
			      break;
			    case 91: 
			      str = handleDate(paramResultSet, paramInt2);
			      break;
			    case 92: 
			      str = handleTime(paramResultSet.getTime(paramInt2));
			      break;
			    case 93: 
			      str = handleTimestamp(paramResultSet.getTimestamp(paramInt2));
			      break;
			    case -16: 
			    case -15: 
			    case -9: 
			    case -1: 
			    case 1: 
			    case 12: 
			      str = paramResultSet.getString(paramInt2);
			      break;
			    default: 
			      str = "";
			    }
			    if (str == null) {
			      str = "";
			    }
			    return str;
			  }
			  
			  private static String read(Clob paramClob)
			    throws SQLException, IOException
			  {
			    StringBuilder localStringBuilder = new StringBuilder((int)paramClob.length());
			    Reader localReader = paramClob.getCharacterStream();
			    char[] arrayOfChar = new char[2048];
			    int i;
			    while ((i = localReader.read(arrayOfChar, 0, arrayOfChar.length)) != -1) {
			      localStringBuilder.append(arrayOfChar, 0, i);
			    }
			    return localStringBuilder.toString();
			  }
			
private String handleTimestamp(Timestamp paramTimestamp)
{
  SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
  return paramTimestamp == null ? null : localSimpleDateFormat.format(paramTimestamp);
}

private String handleObject(Object paramObject)
{
  return paramObject == null ? "" : String.valueOf(paramObject);
}

/*private String handleBigDecimal(BigDecimal paramBigDecimal)
{
  return paramBigDecimal == null ? "" : paramBigDecimal.toString();
}*/

private String handleDouble(ResultSet paramResultSet, int paramInt) throws SQLException
{
	DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	double d = paramResultSet.getDouble(paramInt);
	String value = decimalFormat.format(d);
	logger.info(">>>>>> value is >>>>>>"+value.toString());
	return paramResultSet.wasNull() ? "" : value.toString();
}

private String handleLong(ResultSet paramResultSet, int paramInt)
  throws SQLException
{
  long l = paramResultSet.getLong(paramInt);
  return paramResultSet.wasNull() ? "" : Long.toString(l);
}

private String handleInteger(ResultSet paramResultSet, int paramInt)
  throws SQLException
{
  int i = paramResultSet.getInt(paramInt);
  return paramResultSet.wasNull() ? "" : Integer.toString(i);
}

private String handleDate(ResultSet paramResultSet, int paramInt)
  throws SQLException
{
  Date localDate = paramResultSet.getDate(paramInt);
  String str = null;
  if (localDate != null)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    str = localSimpleDateFormat.format(localDate);
  }
  return str;
}

private String handleTime(Time paramTime)
{
  return paramTime == null ? null : paramTime.toString();
}

  /*public static final int CLOBBUFFERSIZE = 2048;
  static final int NVARCHAR = -9;
  static final int NCHAR = -15;
  static final int LONGNVARCHAR = -16;
  static final int NCLOB = 2011;
  private String DEFAULT_DATE_FORMAT = "dd-MMM-yyyy";
  private String DEFAULT_TIMESTAMP_FORMAT = "dd-MMM-yyyy HH:mm:ss";*/
  
  /*private static String read(Clob c)
    throws SQLException, IOException
  {
    StringBuilder sb = new StringBuilder((int)c.length());
    Reader r = c.getCharacterStream();
    char[] cbuf = new char[2048];
    int n;
    while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
      sb.append(cbuf, 0, n);
    }
    return sb.toString();
  }
  
  public String[] getColumnNames(ResultSet rs)
    throws SQLException
  {
    List<String> names = new ArrayList();
    ResultSetMetaData metadata = rs.getMetaData();
    for (int i = 0; i < metadata.getColumnCount(); i++) {
      names.add(metadata.getColumnName(i + 1));
    }
    String[] nameArray = new String[names.size()];
    return (String[])names.toArray(nameArray);
  }
  
  public String[] getColumnValues(ResultSet rs)
    throws SQLException, IOException
  {
    return getColumnValues(rs, false, DEFAULT_DATE_FORMAT, DEFAULT_TIMESTAMP_FORMAT);
  }
  
  public String[] getColumnValues(ResultSet rs, boolean trim)
    throws SQLException, IOException
  {
    return getColumnValues(rs, trim, DEFAULT_DATE_FORMAT, DEFAULT_TIMESTAMP_FORMAT);
  }
  
  public String[] getColumnValues(ResultSet rs, boolean trim, String dateFormatString, String timeFormatString)
    throws SQLException, IOException
  {
    List<String> values = new ArrayList();
    ResultSetMetaData metadata = rs.getMetaData();
    for (int i = 0; i < metadata.getColumnCount(); i++) {
      values.add(getColumnValue(rs, metadata.getColumnType(i + 1), i + 1, trim, dateFormatString, timeFormatString));
    }
    String[] valueArray = new String[values.size()];
    return (String[])values.toArray(valueArray);
  }
  
  private String handleObject(Object obj)
  {
    return obj == null ? "" : String.valueOf(obj);
  }
  
  private String handleBigDecimal(BigDecimal decimal)
  {
    return decimal == null ? "" : decimal.toString();
  }
  
  private String handleLong(ResultSet rs, int columnIndex)
    throws SQLException
  {
    long lv = rs.getLong(columnIndex);
    return rs.wasNull() ? "" : Long.toString(lv);
  }
  
  private String handleInteger(ResultSet rs, int columnIndex)
    throws SQLException
  {
    int i = rs.getInt(columnIndex);
    return rs.wasNull() ? "" : Integer.toString(i);
  }
  
  private String handleDate(ResultSet rs, int columnIndex, String dateFormatString)
    throws SQLException
  {
    Date date = rs.getDate(columnIndex);
    String value = null;
    if (date != null)
    {
      try{   
      		  SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
    		  value = dateFormat.format(date);
      }catch(Exception e) {
    	  logger.error("Exception is :: "+e);
    	  return date.toString();
      }
      
    }
    return value;
  }
  
  private String handleTime(Time time)
  {
    return time == null ? null : time.toString();
  }
  
  private String handleTimestamp(Timestamp timestamp, String timestampFormatString)
  {
	  try{		   
		  SimpleDateFormat timeFormat = new SimpleDateFormat(timestampFormatString);
		  return timestamp == null ? null : timeFormat.format(timestamp);
	  }catch(Exception e) {
		  logger.error("** Exception : " , e);
		  return timestamp.toString();
	  }
  }
  
  private String getColumnValue(ResultSet rs, int colType, int colIndex, boolean trim, String dateFormatString, String timestampFormatString)
    throws SQLException, IOException
  {
    String value = "";
    switch (colType)
    {
    case -7: 
    case 2000: 
      value = handleObject(rs.getObject(colIndex));
      break;
    case 16: 
      boolean b = rs.getBoolean(colIndex);
      value = Boolean.valueOf(b).toString();
      break;
    case 2005: 
    case 2011: 
      Clob c = rs.getClob(colIndex);
      if (c != null) {
        value = read(c);
      }
      break;
    case -5: 
      value = handleLong(rs, colIndex);
      break;
    case 2: 
    case 3: 
    case 6: 
    case 7: 
    case 8: 
      value = handleBigDecimal(rs.getBigDecimal(colIndex));
      break;
    case -6: 
    case 4: 
    case 5: 
      value = handleInteger(rs, colIndex);
      break;
    case 91: 
      value = handleDate(rs, colIndex, dateFormatString);
      break;
    case 92: 
      value = handleTime(rs.getTime(colIndex));
      break;
    case 93: 
      value = handleTimestamp(rs.getTimestamp(colIndex), timestampFormatString);
      break;
    case -16: 
    case -15: 
    case -9: 
    case -1: 
    case 1: 
    case 12: 
      String columnValue = rs.getString(colIndex);
      if ((trim) && (columnValue != null)) {
        value = columnValue.trim();
      } else {
        value = columnValue;
      }
      break;
    default: 
      value = "";
    }
    if (value == null) {
      value = "";
    }
    return value;
  }

public void setTimestampFormat(String timestampFormat) {
	// TODO Auto-generated method stub
	DEFAULT_TIMESTAMP_FORMAT = timestampFormat;
}

public void setDateFormat(String dateFormat) {
	// TODO Auto-generated method stub
	DEFAULT_DATE_FORMAT = dateFormat;
}*/
}
