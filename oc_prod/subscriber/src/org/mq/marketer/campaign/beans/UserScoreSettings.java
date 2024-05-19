package org.mq.marketer.campaign.beans;

public class UserScoreSettings implements java.io.Serializable {


	private Long id;
	private String groupName;
	private String condition;
	private String dataOne;
	private String dataTwo;
	private Integer score;
	private Integer maxScore;
	private String type;
	private Users user;
	
	public UserScoreSettings(){}
	
	public UserScoreSettings(Users userId) {
		
		this.user = userId;
	}
	
	
	public UserScoreSettings(String groupName,String condition,String dataOne,
			String dataTwo,Integer score,Integer maxScore,String Type,Users userId){
		this.groupName=groupName;
		this.condition=condition;
		this.dataOne=dataOne;
		this.dataTwo=dataTwo;
		this.score=score;
		this.maxScore=maxScore;
		this.type=Type;
		this.user=userId;
	}
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getGroupName() {
		return this.groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getCondition() {
		return this.condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getDataOne() {
		return this.dataOne;
	}

	public void setDataOne(String dataOne) {
		this.dataOne = dataOne;
	}

	public String getDataTwo() {
		return dataTwo;
	}

	public void setDataTwo(String dataTwo) {
		this.dataTwo = dataTwo;
	}

	public Integer getScore() {
		return this.score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getMaxScore() {
		return this.maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return ">> "+
		groupName+"::"+
		condition+"::"+
		dataOne+"::"+
		dataTwo+"::"+
		score+"::"+
		maxScore+"::"+
		type;
	}
}
