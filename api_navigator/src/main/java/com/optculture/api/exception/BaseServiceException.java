package com.optculture.api.exception;

public class BaseServiceException extends Exception {
	
	private static final long serialVersionUID = 317442828920204811L;

	public BaseServiceException(String str){
		super(str);
	}
	
	public BaseServiceException(String str, Throwable thr){
		super(str, thr);
	}

}
