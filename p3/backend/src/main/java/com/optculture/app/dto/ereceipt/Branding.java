package com.optculture.app.dto.ereceipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branding {
	private String companyLogo;
	private String fontName;
	private String fontURL;
	private String coverImage;
	private String logoImage;
	private String homePageColorCode;
	private String balanceCardThemeColor;
	private String balanceCardTextColor;
	private String bannerName;
	private String currencySymbol;
	private String urlStr;
	private String referralImage;
}
