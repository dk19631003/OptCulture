package org.mq.loyality.common.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.CharacterCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;




public class CharacterCodesDao{
	
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private SessionFactory sessionFactoryForDML;

	
	public CharacterCodesDao() {}
	
	
	@Transactional("txMngrForDML")
	public void saveOrUpdate(CharacterCodes characterCodes) {
        sessionFactoryForDML.getCurrentSession().saveOrUpdate(characterCodes);
    }
	 @Transactional
	public List<CharacterCodes> findAll() {
		String query = "FROM CharacterCodes";
		return  sessionFactory.getCurrentSession().createQuery(query).list();
		
    }
}
