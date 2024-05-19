package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.CountryReceivingNumbers;

public class CountryReceivingNumbersDao extends AbstractSpringDao {
	
	

	
	 
	/*public void delete(CountryReceivingNumbers countryReceivingNumbers){
        super.delete(countryReceivingNumbers);
	}
	 
	 public void saveByCollection(List<CountryReceivingNumbers> countryReceivingNumbers) {
	        super.saveByCollection(countryReceivingNumbers);//OrUpdate(obj);(countryReceivingNumbers);
	    }	

    public void saveOrUpdate(CountryReceivingNumbers countryReceivingNumbers) {
        super.saveOrUpdate(countryReceivingNumbers);
    }	*/
 
	 public  List<String> getReceivingNumByCountry(String country, String recvNumType)  {
		 
		 List<String> recvNums = null;
		 String query = "SELECT receivingNumber FROM CountryReceivingNumbers where country = '"+country+ "' AND recvNumType = '" +recvNumType+"' ";
		 recvNums = executeQuery(query);
		 return recvNums;
		 
	 }
	 
	 public List<CountryReceivingNumbers> findBy(String country) {
		 
		 String qry = "FROM CountryReceivingNumbers WHERE country ='"+country+"'";//  AND recvNumType = '"+recvNumType+"' AND receivingNumber = '"+recvNumber+"' ";
			
			List<CountryReceivingNumbers>  retObjectsList  = executeQuery(qry);
			
			if(retObjectsList != null && retObjectsList.size() > 0) {
				
				return retObjectsList;
			}else return null;
		 
	 }
	 
	 public CountryReceivingNumbers findBy(String country, String recvNumType , String recvNumber) throws Exception{
		 
		 String qry = "FROM CountryReceivingNumbers WHERE country ='"+country+"'  AND recvNumType = '"+recvNumType+"' AND receivingNumber = '"+recvNumber+"' ";
			
			List<CountryReceivingNumbers>  retObjectsList  = executeQuery(qry);
			
			if(retObjectsList != null && retObjectsList.size() > 0) {
				
				return retObjectsList.get(0);
			}else return null;
	 }
	 
	 public  CountryReceivingNumbers getReceivingNumByCountryAndGateway(String country, Long gateway)  {
		 
		 String qry = "SELECT receivingNumber FROM CountryReceivingNumbers where country = '"+country+ "' AND gateway = '" +gateway+"' ";
		 //CountryReceivingNumbers  retObjectsList  = (CountryReceivingNumbers) executeQuery(query);
			
		 List<CountryReceivingNumbers>  retObjectsList  = executeQuery(qry);
			
			if(retObjectsList != null && retObjectsList.size() > 0) {
				
				return retObjectsList.get(0);
			}else return null;
	 }
}
