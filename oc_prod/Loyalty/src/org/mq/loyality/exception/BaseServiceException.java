/**
 * 
 */
package org.mq.loyality.exception;

/**
 * @author manjunath.nunna
 *
 */
public class BaseServiceException extends Exception{
	
	
	public BaseServiceException(String str){
		super(str);
	}
	
	public BaseServiceException(String str, Throwable thr){
		super(str, thr);
	}

}
