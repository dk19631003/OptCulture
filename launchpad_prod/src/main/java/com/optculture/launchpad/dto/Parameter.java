package com.optculture.launchpad.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parameter {
	
    private String type;
    private String text;
    //CM
	private Map<String, Object> media; // For dynamic part
	//META
	private Map<String, Object> image;
	private Map<String, Object> video;
	private Map<String, Object> document;

    public Parameter(String type, String text) {
		super();
		this.type = type;
		this.text = text;
	}

	public Parameter(String type, Map<String, Object> media) {
		super();
		this.type = type;
		this.media = media;
	}
}
