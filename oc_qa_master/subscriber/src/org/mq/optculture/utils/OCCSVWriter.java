package org.mq.optculture.utils;



import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

import au.com.bytecode.opencsv.CSVWriter;
/**
 * 
 * @author proumyaa
 *unless we have some customizations , no specific methods are required in this class.<br>
 *Example : other than any specific date formats,field seperators, enclosing characters.
 */
public class OCCSVWriter extends CSVWriter
  
{
  /*public static final int INITIAL_STRING_SIZE = 128;
  public static final char DEFAULT_ESCAPE_CHARACTER = '"';
  public static final char DEFAULT_SEPARATOR = ',';
  public static final char DEFAULT_QUOTE_CHARACTER = '"';
  public static final char NO_QUOTE_CHARACTER = 'u\000';
  public static final char NO_ESCAPE_CHARACTER = 'u\000';
  public static final String DEFAULT_LINE_END = "\n";
  private Writer rawWriter;
  private PrintWriter pw; */
  private char separator;
  private char quotechar;
  private char escapechar;
  private String lineEnd;
  private OCResultSetHelperService resultService = new OCResultSetHelperService();
  
  public OCCSVWriter(Writer writer)
  {
    super(writer, DEFAULT_SEPARATOR);
    this.separator = ',';
    this.quotechar = '"';
    this.escapechar = '"';
    this.lineEnd = "\n";
  }
  
  /*
   * This method writes resultset data to file by ignoring specific field data
   */
  
  public void writeAll(ResultSet paramResultSet, boolean paramBoolean, int indexToIgnore)
		    throws SQLException, IOException
		  {
		    if (paramBoolean) {
		      writeColumnNames(paramResultSet);
		    }
		    while (paramResultSet.next()) {
		      writeNext(this.resultService.getColumnValues(paramResultSet,indexToIgnore));
		    }
		  }
  public void writeAll(ResultSet paramResultSet, boolean paramBoolean, int[] indexToIgnore)
		    throws SQLException, IOException
		  {
		    if (paramBoolean) {
		      writeColumnNames(paramResultSet);
		    }
		    while (paramResultSet.next()) {
		      writeNext(this.resultService.getColumnValues(paramResultSet,indexToIgnore));
		    }
		  }
  
  public void writeAll(ResultSet paramResultSet, boolean paramBoolean, int indexToIgnore, int multipleValuesColumn)
		    throws SQLException, IOException
		  {
		    if (paramBoolean) {
		      writeColumnNames(paramResultSet);
		    }
		    while (paramResultSet.next()) {
		      writeNext(this.resultService.getColumnValues(paramResultSet,indexToIgnore,multipleValuesColumn));
		    }
		  }
   
  
  /*public void setDateFormat(String dateFormat) {
	  resultService.setDateFormat(dateFormat);
  }
  public void setTimestampFormat(String timestampFormat) {
	  
	  resultService.setTimestampFormat(timestampFormat);
  }
 */
  
  
  
 /* public OCCSVWriter(Writer writer, char separator)
  {
    this(writer, separator, '"');
  }
  
  public OCCSVWriter(Writer writer, char separator, char quotechar)
  {
    this(writer, separator, quotechar, '"');
  }
  
  public OCCSVWriter(Writer writer, char separator, char quotechar, char escapechar)
  {
    this(writer, separator, quotechar, escapechar, "\n");
  }
  
  public OCCSVWriter(Writer writer, char separator, char quotechar, String lineEnd)
  {
    this(writer, separator, quotechar, '"', lineEnd);
  }*/
  
 /* public OCCSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd)
  {
    super.rawWriter = writer;
    this.pw = new PrintWriter(writer);
    this.separator = separator;
    this.quotechar = quotechar;
    this.escapechar = escapechar;
    this.lineEnd = lineEnd;
  }*/
  
 /* public void writeAll(List<String[]> allLines, boolean applyQuotesToAll)
  {
    for (String[] line : allLines) {
      writeNext(line, applyQuotesToAll);
    }
  }*/
  
 /* public void writeAll(List<String[]> allLines)
  {
    for (String[] line : allLines) {
      writeNext(line);
    }
  }*/
  
  /*protected void writeColumnNames(ResultSet rs)
    throws SQLException
  {
    writeNext(this.resultService.getColumnNames(rs));
  }
  
  public void writeAll(ResultSet rs, boolean includeColumnNames)
    throws SQLException, IOException
  {
    writeAll(rs, includeColumnNames, false);
  }
  
  public void writeAll(ResultSet rs, boolean includeColumnNames, boolean trim)
    throws SQLException, IOException
  {
    if (includeColumnNames) {
      writeColumnNames(rs);
    }
    while (rs.next()) {
      writeNext(this.resultService.getColumnValues(rs, trim));
    }
  }
  
  public void writeNext(String[] nextLine, boolean applyQuotesToAll)
  {
    if (nextLine == null) {
      return;
    }
    StringBuilder sb = new StringBuilder(128);
    for (int i = 0; i < nextLine.length; i++)
    {
      if (i != 0) {
        sb.append();
      }
      String nextElement = nextLine[i];
      if (nextElement != null)
      {
        Boolean stringContainsSpecialCharacters = Boolean.valueOf(stringContainsSpecialCharacters(nextElement));
        if (((applyQuotesToAll) || (stringContainsSpecialCharacters.booleanValue())) && (this.quotechar != 0)) {
          sb.append(this.quotechar);
        }
        if (stringContainsSpecialCharacters.booleanValue()) {
          sb.append(processLine(nextElement));
        } else {
          sb.append(nextElement);
        }
        if (((applyQuotesToAll) || (stringContainsSpecialCharacters.booleanValue())) && (this.quotechar != 0)) {
          sb.append(this.quotechar);
        }
      }
    }
    sb.append(this.lineEnd);
    this.pw.write(sb.toString());
  }
  
  public void writeNext(String[] nextLine)
  {
    writeNext(nextLine, true);
  }
  
  private boolean stringContainsSpecialCharacters(String line)
  {
    return (line.indexOf(DEFAULT_QUOTE_CHARACTER) != -1) 
    		|| (line.indexOf(DEFAULT_QUOTE_CHARACTER) != -1) 
    		|| (line.indexOf(DEFAULT_QUOTE_CHARACTER) != -1) 
    		|| (line.contains("\n")) || (line.contains("\r"));
  }
  
  protected StringBuilder processLine(String nextElement)
  {
    StringBuilder sb = new StringBuilder(128);
    for (int j = 0; j < nextElement.length(); j++)
    {
      char nextChar = nextElement.charAt(j);
      if ((this.escapechar != 0) && (nextChar == this.quotechar)) {
        sb.append(this.escapechar).append(nextChar);
      } else if ((this.escapechar != 0) && (nextChar == this.escapechar)) {
        sb.append(this.escapechar).append(nextChar);
      } else {
        sb.append(nextChar);
      }
    }
    return sb;
  }
  
  public void flush()
    throws IOException
  {
    this.pw.flush();
  }
  */
  /*public void close()
    throws IOException
  {
    flush();
    this.pw.close();
    this.rawWriter.close();
  }
  
  public boolean checkError()
  {
    return this.pw.checkError();
  }*/
  
  public void setResultService(OCResultSetHelperService resultService)
  {
    this.resultService = resultService;
  }
  
 /* public void flushQuietly()
  {
    try
    {
      flush();
    }
    catch (IOException e) {}
  }*/
}
