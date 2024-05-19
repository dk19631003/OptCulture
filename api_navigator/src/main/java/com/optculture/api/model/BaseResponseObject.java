package com.optculture.api.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseResponseObject implements Serializable{
	
	private static final long serialVersionUID = 3174428992805704811L;

	private String action;
	private String responseCode;
	private String responseDesc;
	private Object responseObject;
	private String jsonValue;
	private String responseStr;

}
