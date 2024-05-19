package org.mq.marketer.campaign.controller.contacts;


public enum HomesPassedEnum {

	
	/*COUNTRY(1, null, 2, " SELECT h.country, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.country,'--')) " +" as address ", " GROUP BY h.country ", "country"  ),
	
	
	STATE(2, COUNTRY, 3, " SELECT h.state, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.state,'--'),', ', IFNULL(h.country,'--')) " +" as address ", " GROUP BY h.country, h.state ", "state"  ),
	
	DISTRICT(3, STATE, 4, " SELECT h.district, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.district,'--'), ', ',IFNULL(h.state,'--'),', ', IFNULL(h.country,'--')) " +" as address ", " GROUP BY h.country, h.state,  h.district", "district"  ),
	
	CITY(4, DISTRICT, 5, " SELECT h.city, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.city,'--'), ', ', IFNULL(h.district,'--'), ', ',IFNULL(h.state,'--'),', ', IFNULL(h.country,'--'))  " +" as address ", " GROUP BY h.country, h.state, h.district, h.city", "city"  ),
	
	ZIP(5, CITY, 6, " SELECT h.zip, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.zip,'--'), ', ',IFNULL(h.city,'--'), ', ', IFNULL(h.district,'--'), ', ',IFNULL(h.state,'--'),', ', IFNULL(h.country,'--')) " +" as address ", " GROUP BY h.country, h.state, h.district, h.city, h.zip", "zip"  ),
	
	AREA(6, ZIP, 7, " SELECT h.area, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.area,'--'), ', ',IFNULL(h.zip,'--'), ', ', IFNULL(h.city,'--'),', ', IFNULL(h.district,'--'),', ',IFNULL(h.state,'--'),', ', IFNULL(h.country,'--')) " +" as address ", " GROUP BY h.country, h.state,h.district, h.city,h.zip, h.area ", "area"  ),
	
	STREET(7, AREA, 8, " SELECT h.street, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.street,'--'), ', ', IFNULL(h.area,'--'), ', ',IFNULL(h.zip,'--'), ', ',IFNULL(h.city,'--'),', ', IFNULL(h.district,'--'),', ',IFNULL(h.state,'--'),', ', IFNULL(h.country,'--')) " +" as address ", " GROUP BY h.country, h.state, h.district, h.city,h.zip, h.area, h.street ", "street"  ),
	
	ADDRESSONE(8, STREET, 9, " SELECT h.address_one, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.address_one,'--'),  ', ', IFNULL(h.street,'--'), ', ', IFNULL(h.area,'--'), ', ',IFNULL(h.zip,'--'), ', ', IFNULL(h.city,'--'),', ', IFNULL(h.district,'--'), ', ',IFNULL(h.state,'--'),', ', IFNULL(h.country,'--')) " +" as address ", " GROUP BY h.country, h.state, h.district, h.city,h.zip, h.area, h.street, h.address_one ", "address_one"  ),
	
	ADDRESSTWO(9, ADDRESSONE, -1, " SELECT h.address_two, COUNT(distinct h.address_unit_id) AS allcount, CONCAT(IFNULL(h.address_two,'--'), ', ',IFNULL(h.address_one,'--'), ', ', IFNULL(h.street,'--'), ', ', IFNULL(h.area,'--'), ', ',IFNULL(h.zip,'--'), ', ', IFNULL(h.city,'--'), ', ', IFNULL(h.district,'--'),', ',IFNULL(h.state,'--'),', ', IFNULL(h.country,'--')) " +" as address ", " GROUP BY h.country, h.state, h.district, h.city,h.zip, h.area, h.street,h.address_one, h.address_two", "address_two"  );
	*/
	
	COUNTRY(1, null, 2, " SELECT h.country, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "country", "Country"  ),
	
	
	STATE(2, COUNTRY, 3, " SELECT h.state, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "state", "State"  ),
	
	DISTRICT(3, STATE, 4, " SELECT h.district, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "district", "District"  ),
	
	CITY(4, DISTRICT, 5, " SELECT h.city, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "city", "City"  ),
	
	AREA(5, CITY, 6, " SELECT h.area, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "area", "Area"  ),

	ZIP(6, AREA, 7, " SELECT h.zip, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "zip", "ZIP"  ),
	

	STREET(7, ZIP, 8, " SELECT h.street, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "street", "Street"  ),
	
	ADDRESSONE(8, STREET, 9, " SELECT h.address_one, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "address_one", "Address One"  ),
	
	ADDRESSTWO(9, ADDRESSONE, -1, " SELECT h.address_two, COUNT(distinct h.address_unit_id) AS allcount, <CONTACTCOUNT> CONCAT(<CONCATSTR>) " +" as address ", " GROUP BY <GROUPBYSTR> ", "address_two" , "Address Two" );
	
	
	
	private int serealNum;
	private HomesPassedEnum parentEnum;
	//private HomesPassedEnum childEnum;
	private int childCode; 
	


	private String selectQry;
	private  String groupByStr;
	private  String colLabel; 
	private String dispLabel;
	
	
	public String getDispLabel() {
		return dispLabel;
	}


	public void setDispLabel(String dispLabel) {
		this.dispLabel = dispLabel;
	}


	public int getSerealNum() {
		return serealNum;
	}


	public void setSerealNum(int serealNum) {
		this.serealNum = serealNum;
	}


	public HomesPassedEnum getParentEnum() {
		return parentEnum;
	}


	public void setParentEnum(HomesPassedEnum parentEnum) {
		this.parentEnum = parentEnum;
	}


	public int getChildCode() {
		return childCode;
	}


	public void setChildCode(int childCode) {
		this.childCode = childCode;
	}
	
	/*public HomesPassedEnum getChildEnum() {
		return childEnum;
	}


	public void setChildEnum(HomesPassedEnum childEnum) {
		this.childEnum = childEnum;
	}
*/

	public String getSelectQry() {
		return selectQry;
	}


	public void setSelectQry(String selectQry) {
		this.selectQry = selectQry;
	}


	public String getGroupByStr() {
		return groupByStr;
	}


	public void setGroupByStr(String groupByStr) {
		this.groupByStr = groupByStr;
	}


	public String getColLabel() {
		return colLabel;
	}


	public void setColLabel(String colLabel) {
		this.colLabel = colLabel;
	}
	
	
	private HomesPassedEnum(int serealNum, HomesPassedEnum parentEnum, int childCode, String selectQry, String groupByStr, String colLabel, String dispLabel ){
		
		this.serealNum = serealNum;
		this.parentEnum = parentEnum;
		//this.childEnum = childEnum;
		this.childCode = childCode;
		this.selectQry = selectQry;
		this.groupByStr = groupByStr;
		this.colLabel = colLabel;
		this.dispLabel = dispLabel;
		
		
		
		
		
	}
	
	public HomesPassedEnum getChildEnum (int childCode) {
		
		
		HomesPassedEnum[] EnumArr = HomesPassedEnum.values();
		for (HomesPassedEnum homesPassedEnum : EnumArr) {
			//logger.info("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
			
			if(homesPassedEnum.getSerealNum() == childCode) {
				
				
				return homesPassedEnum;
			}//if
			
		}//for
		
		return null;
	}
	
	public static HomesPassedEnum getEnumByCode (int code) {
		
		
		HomesPassedEnum[] EnumArr = HomesPassedEnum.values();
		for (HomesPassedEnum homesPassedEnum : EnumArr) {
			//logger.info("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
			
			if(homesPassedEnum.getSerealNum() == code) {
				
				
				return homesPassedEnum;
			}//if
			
		}//for
		
		return null;
	}
	
}
