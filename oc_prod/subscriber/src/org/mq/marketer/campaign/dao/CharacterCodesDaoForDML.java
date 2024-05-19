package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.CharacterCodes;



public class CharacterCodesDaoForDML extends AbstractSpringDaoForDML{

	
	public CharacterCodesDaoForDML() {}
	
	
	public void saveOrUpdate(CharacterCodes characterCodes) {
        super.saveOrUpdate(characterCodes);
    }
	
	/*public List<CharacterCodes> findAll() {
		
		String query = "FROM CharacterCodes";
		
		return executeQuery(query);
		
    }*/
}
