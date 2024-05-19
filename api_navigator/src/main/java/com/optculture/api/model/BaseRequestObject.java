package com.optculture.api.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseRequestObject implements Serializable {
	
	private static final long serialVersionUID = -3551817185009000029L;
	private String action;
	private String jsonValue;
	private String msgContent;

	private String timeFormat;

}
