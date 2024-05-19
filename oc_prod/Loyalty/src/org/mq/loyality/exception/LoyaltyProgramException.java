package org.mq.loyality.exception;

public class LoyaltyProgramException extends Exception{

	private static final long serialVersionUID = 6352070784983791803L;

	public LoyaltyProgramException(String message) {
		super(message);
	}
	
	public LoyaltyProgramException(String message, Throwable thr) {
		super(message, thr); 
	}
}
