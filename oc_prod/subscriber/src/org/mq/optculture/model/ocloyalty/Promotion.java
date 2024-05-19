package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

public class Promotion {

	private String name;
	
	public Promotion(){
		//Default Constructor
	}

	public String getName() {
		return name;
	}
	@XmlElement(name = "name")
	public void setName(String name) {
		this.name = name;
	}

		
}
