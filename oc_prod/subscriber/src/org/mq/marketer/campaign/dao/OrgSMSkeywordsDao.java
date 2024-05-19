package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.general.Constants;;

public class OrgSMSkeywordsDao extends AbstractSpringDao {




	public OrgSMSkeywordsDao() {

	}

	/*public void delete(OrgSMSkeywords orgSMSkeywords){
		super.delete(orgSMSkeywords);
	}*/


	/*public void saveOrUpdate(OrgSMSkeywords smsKeywords) {
	        super.saveOrUpdate(smsKeywords);
	    }*/

	public List<OrgSMSkeywords> getUserOrgSMSKeyWords(Long orgId, int startIndex, int count,String orderby_colName,String desc_Asc) {

		List<OrgSMSkeywords> keywordsList = null;

		//		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND validUpto >= now() ";
		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue() +"order by "+orderby_colName+" "+desc_Asc;

		keywordsList = executeQuery(qry, startIndex, count);

		return keywordsList;



	}

	public List<OrgSMSkeywords> getUserOrgSMSKeyWords(Long orgId) {

		List<OrgSMSkeywords> keywordsList = null;

		//		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND validUpto >= now() ";
		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue();

		keywordsList = executeQuery(qry);

		return keywordsList;



	}

	public List<OrgSMSkeywords> getUserOrgEXPSMSKeyWords(Long orgId) {

		List<OrgSMSkeywords> keywordsList = null;

		//		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND validUpto >= now() ";
		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue() + "AND status IN('" + Constants.SMS_KEYWORD_EXPIRED + "'," + "'"+ Constants.KEYWORD_STATUS_PENDING +"')";

		keywordsList = executeQuery(qry);

		return keywordsList;



	}


	public int getUserOrgSMSKeyWordsCount(Long orgId) {


		//		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND validUpto >= now() ";
		String qry = "SELECT COUNT(keywordId) FROM OrgSMSkeywords WHERE orgId="+orgId.longValue();

		List<Long> list = getHibernateTemplate().find(qry);

		if(list.size() > 0) {

			return ((Long)list.get(0)).intValue();
		}
		return 0;

	}

	public boolean findByKeyword(String keyword) {

		keyword = StringEscapeUtils.escapeSql(keyword);
		String qry = "FROM OrgSMSkeywords WHERE keyword='"+keyword+"'"; 

		List<OrgSMSkeywords> retList = getHibernateTemplate().find(qry);
		if(retList != null && retList.size() > 0) return true;
		else return false;




	}

	public List<Object[]> findExistencePromoKeyword(String keyword, String shortCode) {

		keyword = StringEscapeUtils.escapeSql(keyword);
		/*String qry = "FROM OrgSMSkeywords WHERE keyword='"+keyword+"' AND shortCode='"+shortCode+"'"; 



		List<OrgSMSkeywords> retList = getHibernateTemplate().find(qry);
		if(retList != null && retList.size() > 0) return true;
		else return false;*/

		List<Object[]> keywordsList = null;
		String qry = " FROM   OrgSMSkeywords o WHERE (" + "o.keyword='"+keyword+"' AND o.shortCode='"+shortCode+"' )";

		keywordsList = executeQuery(qry);

		return keywordsList;

	}

	public List<Object[]> findExistenceOtherKeyword(String keyword, String shortCode) {

		keyword = StringEscapeUtils.escapeSql(keyword);
		/*String qry = "FROM OrgSMSkeywords WHERE keyword='"+keyword+"' AND shortCode='"+shortCode+"'"; 



		List<OrgSMSkeywords> retList = getHibernateTemplate().find(qry);
		if(retList != null && retList.size() > 0) return true;
		else return false;*/

		List<Object[]> keywordsList = null;
		String qry = " FROM   SMSSettings s WHERE (s.keyword='"+keyword+"' AND s.shortCode='"+shortCode+"')";

		keywordsList = executeQuery(qry);

		return keywordsList;

	}


	public List<Object[]> findExistenceKeyword(String keyword, String shortCode) {

		keyword = StringEscapeUtils.escapeSql(keyword);
		/*String qry = "FROM OrgSMSkeywords WHERE keyword='"+keyword+"' AND shortCode='"+shortCode+"'"; 



		List<OrgSMSkeywords> retList = getHibernateTemplate().find(qry);
		if(retList != null && retList.size() > 0) return true;
		else return false;*/

		List<Object[]> keywordsList = null;
		String qry = " FROM   SMSSettings s,OrgSMSkeywords o WHERE (s.keyword='"+keyword+"' AND s.shortCode='"+shortCode+"') OR (" +
				" o.keyword='"+keyword+"' AND o.shortCode='"+shortCode+"' )";

		keywordsList = executeQuery(qry);

		return keywordsList;

	}

	public int findAllCountByOrg(Long orgId) {

		//String qry = "SELECT COUNT(keywordId) FROM OrgSMSkeywords WHERE orgId="+orgId.longValue();


		String qry=	" SELECT COUNT(DISTINCT k.keywordId) FROM OrgSMSkeywords k, ClickaTellSMSInbound i " +
				" WHERE i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords ";

		List<Long> list = getHibernateTemplate().find(qry);

		if(list.size() > 0) {

			return ((Long)list.get(0)).intValue();
		}
		return 0;

	}



	public int findAllResponseCountByOrg(Long orgId) {

		//String qry = "SELECT COUNT(keywordId) FROM OrgSMSkeywords WHERE orgId="+orgId.longValue();


		String qry=	"SELECT COUNT( i.inboundMsgId) FROM OrgSMSkeywords k, ClickaTellSMSInbound i " +
				" WHERE i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords ";


		List<Long> list = getHibernateTemplate().find(qry);

		if(list.size() > 0) {

			return ((Long)list.get(0)).intValue();
		}
		return 0;

	}

	public int findAllKeywordCountByOrg(Long orgId) {


		String qry=	"SELECT COUNT(DISTINCT keywordId) FROM OrgSMSkeywords where orgId = "+ orgId.longValue();


		List<Long> list = getHibernateTemplate().find(qry);

		if(list.size() > 0) {

			return ((Long)list.get(0)).intValue();
		}
		return 0;

	}

	public int findAllResponseCountByOrg(String keyword, String shortCode) {

		//String qry = "SELECT COUNT(keywordId) FROM OrgSMSkeywords WHERE orgId="+orgId.longValue();


		String qry=	"SELECT COUNT( i.inboundMsgId) FROM OrgSMSkeywords k, ClickaTellSMSInbound i " +
				" WHERE i.orgId IS NOT NULL AND i.usedKeyWords='"+keyword+"' AND i.moTo='"+shortCode+"'  AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords ";


		List<Long> list = getHibernateTemplate().find(qry);

		if(list.size() > 0) {

			return ((Long)list.get(0)).intValue();
		}
		return 0;

	}


	public List<Object[]> getKeywordResponseReport(Long orgId, int startIndex, int count) {

		String qry = "SELECT i.moFrom,k.keyword, i.moTo,  i.text, i.timeStamp, i.autoResponse, i.deliveryStatus,i.deliveredTime FROM OrgSMSkeywords k , ClickaTellSMSInbound i" +
				" WHERE i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords ORDER BY i.timeStamp DESC";

		return executeQuery(qry, startIndex, count);


	}

	public List<Object[]> getKeywordResponseReport(Long orgId, String mobNO, int startIndex, int count,String orderby_colName_response,String desc_Asc) {

		String qry = "SELECT i.moFrom,k.keyword, i.moTo, i.text, i.timeStamp, i.autoResponse, i.deliveryStatus,i.deliveredTime FROM OrgSMSkeywords k , ClickaTellSMSInbound i" +
				" WHERE i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords AND i.moFrom like '%" + mobNO + "%' ORDER BY "+orderby_colName_response+" "+desc_Asc;

		return executeQuery(qry, startIndex, count);


	}

	public int getKeywordResponseReportCount(Long orgId, String mobNO) {

		String qry = "SELECT COUNT(i.inboundMsgId) FROM OrgSMSkeywords k , ClickaTellSMSInbound i" +
				" WHERE i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords AND i.moFrom like '%" + mobNO + "%' ";

		List<Long> list = executeQuery(qry);

		if(list.size() > 0) {

			return ((Long)list.get(0)).intValue();
		}
		return 0;


	}

	@SuppressWarnings("unchecked")
	/*public List<Object[]> getUsedKeywordReport(Long orgId, int startIndex, int count) {

		String qry = "SELECT k.keyword,count(i.inboundMsgId) AS no_of_responses FROM OrgSMSkeywords k , ClickaTellSMSInbound i" +
				" WHERE i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords GROUP BY k.keyword";

		return executeQuery(qry, startIndex, count);
		//return getHibernateTemplate().find(qry);


	}*/

	public List<Object[]> getUsedKeywordReport(Long orgId, int startIndex, int count,String orderby_colName,String desc_Asc) {

		String qry = "SELECT k ,count(i.inboundMsgId) AS no_of_responses FROM OrgSMSkeywords k , ClickaTellSMSInbound i" +
				" WHERE i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords GROUP BY k.keyword order by k."+orderby_colName+" "+desc_Asc;

		return executeQuery(qry, startIndex, count);
		//return getHibernateTemplate().find(qry);


	}

	public Long getUsedKeywordReport(Long orgId, String keyword, String shortCode) {

		String qry = "SELECT count(i.inboundMsgId) AS no_of_responses FROM OrgSMSkeywords k , ClickaTellSMSInbound i" +
				" WHERE i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+ "AND k.keyword ='" + keyword + "'"+ "AND k.shortCode ='" + shortCode + "' AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords GROUP BY k.keyword";



		List<Long> list = executeQuery(qry);

		if(list.size() > 0) {
			logger.debug("list of size found in used keyword report  " + list.size());
			return ((Long)list.get(0));
		}

		return (long) 0;
		//return getHibernateTemplate().find(qry);


	}

	public List<OrgSMSkeywords> findAllByReceivedNumber(String receivingNumber) {

		try {
			String qry = "FROM OrgSMSkeywords WHERE shortCode='"+receivingNumber+"'";


			List<OrgSMSkeywords> retList = executeQuery(qry);

			if(retList == null || retList.size() <= 0) {

				return null;
			}else{

				return retList;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}

	}	

	public List<Object[]> getKeywordResponseReport(String keyword, String shortCode, int startIndex, int count) {

		String qry = "SELECT i.moFrom, i.moTo, i.text, i.timeStamp, i.autoResponse, i.deliveryStatus,i.deliveredTime FROM OrgSMSkeywords k , ClickaTellSMSInbound i" +
				" WHERE i.orgId IS NOT NULL AND i.usedKeyWords='"+keyword+"' AND i.moTo='"+shortCode+"' AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
				" AND k.keyword=i.usedKeyWords ORDER BY i.timeStamp DESC";

		return executeQuery(qry, startIndex, count);


	}	

	/*public int deleteById(Long id) throws Exception{

		return executeUpdate("DELETE FROM OrgSMSkeywords WHERE keywordId="+id.longValue());
	}*/



	public List<OrgSMSkeywords> findNonActiveKeywordBy(Long orgId) throws Exception{


		List<OrgSMSkeywords> keywordsList = null;

		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND "
				+ " DATE(NOW()) < DATE(startFrom) AND status='"+Constants.KEYWORD_STATUS_PENDING+"'";

		keywordsList = executeQuery(qry);

		if(keywordsList != null && keywordsList.size() > 0) {

			return keywordsList;

		}else {

			return null;
		}



	}
}
