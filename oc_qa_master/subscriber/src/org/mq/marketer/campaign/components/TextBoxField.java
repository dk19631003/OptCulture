package org.mq.marketer.campaign.components;

public class TextBoxField {

	private final String type="text";
	private String label;
	private String id;
	private boolean required;
	private String defaultValue;
	private String visible;
	//private static int count;
	
	
	
	
	private String mapField;
	public TextBoxField(String id) {
		
		this.label = new String("untitled");
		this.id = id;
		this.required = false;
		this.visible = "";
		this.defaultValue =""; 
		this.mapField="";
		
	}
	public TextBoxField() {
		this.label = new String("untitled");
		this.id ="";
		this.required = false;
		this.visible = "";
		this.defaultValue = "";
		this.mapField="";
	}
	
	
	public TextBoxField(String label, String id, boolean required,String defaultValue,String visible) {
		
		this.label = label;
		this.id = id;
 		this.required = required;
 		this.visible = visible;
		this.defaultValue = defaultValue;
		this.mapField="";
		
	}
	
	public String getMapField() {
		return mapField;
	}
	
	public void setMapField(String mapField) {
		this.mapField = mapField;
	}
	
	public String getType() {
		return type;
	}
	public String getLabel() {
		return label;
	}
	public String getId() {
		return id;
	}
	public boolean isRequired() {
		return required;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getVisible() {
		return visible;
	}
	public void setVisible(String visible) {
		this.visible = visible;
	}
	
	
	
	
}
