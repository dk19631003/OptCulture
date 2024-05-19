package com.optculture.app.dto.campaign.template;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeeEditorBody {

	String templateName;
	String json;
	String html;
	Long commId;
}
