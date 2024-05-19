/**
 * 
 */
package org.mq.optculture.exception;

/**
 * @author manjunath.nunna
 *
 */
public class BaseDAOException extends Exception{
	
	public BaseDAOException(String message){
		super(message);
	}
	
	public BaseDAOException(String message, Throwable throwable){
		super(message, throwable);
	}

}
