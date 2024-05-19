package org.mq.loyality.common.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;

import org.mq.loyality.common.hbmbean.CountryCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;




public class CountryCodeDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public Properties findAllCountryCodes() {
		List<CountryCodes> tempList = sessionFactory.getCurrentSession().createQuery("FROM CountryCodes").list(); 

		Iterator itr = tempList.iterator();
		Properties props = new Properties();


		while(itr.hasNext()){
			CountryCodes cCode = (CountryCodes)itr.next();
			props.put(cCode.getCountryName(), cCode.getCallingCode());
		}

		return props; 
	}

}
