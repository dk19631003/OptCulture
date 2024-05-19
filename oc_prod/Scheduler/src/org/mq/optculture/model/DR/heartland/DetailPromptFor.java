package org.mq.optculture.model.DR.heartland;

import java.util.ArrayList;

public class DetailPromptFor {
	private ArrayList<Object> missing_fields;
    private ArrayList<Object> missing_reason_codes;
	public ArrayList<Object> getMissing_fields() {
		return missing_fields;
	}
	public void setMissing_fields(ArrayList<Object> missing_fields) {
		this.missing_fields = missing_fields;
	}
	public ArrayList<Object> getMissing_reason_codes() {
		return missing_reason_codes;
	}
	public void setMissing_reason_codes(ArrayList<Object> missing_reason_codes) {
		this.missing_reason_codes = missing_reason_codes;
	}

}
