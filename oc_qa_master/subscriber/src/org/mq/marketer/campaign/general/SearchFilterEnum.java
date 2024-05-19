package org.mq.marketer.campaign.general;

public enum SearchFilterEnum {

	CL("Clear All Filters", "/images/ClearFilter.gif", "/images/ClearFilter.gif"),
	
	CS("Contains", "/images/contains.png", "/images/contains_btn.png"),
	SW("Starts With", "/images/starts_with.png", "/images/starts_with_btn.png"),
	EW("Ends With", "/images/ends_with.png", "/images/ends_with_btn.png"),
	EQ("Equals", "/images/equals.png", "/images/equals_btn.png"),
	NE("Not Equals", "/images/not_equals.png", "/images/not_equals_btn.png"),
	
	LT("Less Than", "/images/less_than.png", "/images/less_than_btn.png"),
	GT("Greater Than", "/images/greater_than.png", "/images/greater_than_btn.png"),
	LE("Less Than or Equals", "/images/less_than_eq.png", "/images/less_than_eq_btn.png"),
	GE("Greater Than or Equals", "/images/greater_than_eq.png", "/images/greater_than_eq_btn.png");

	String tooltip;
	String miImage;
	String btnImage;
	
	private SearchFilterEnum(String tooltip, String miImage, String btnImage) {
		this.tooltip = tooltip;
		this.miImage = miImage;
		this.btnImage = btnImage;
	}

	public String getTooltip() {
		return tooltip;
	}

	public String getMiImage() {
		return miImage;
	}

	public String getBtnImage() {
		return btnImage;
	}
	
	public static SearchFilterEnum findByTooltip(String ttStr) {
		SearchFilterEnum sfEnums[] = values();
		for (SearchFilterEnum eachEnum : sfEnums) {
			if(eachEnum.getTooltip().equals(ttStr)) return eachEnum;
		}
		return null;
	}
}
