package org.mq.marketer.campaign.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.beans.Clicks;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.EmailClient;
import org.mq.marketer.campaign.beans.Opens;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class DigiReceiptsReportsController extends AbstractController{
	
	/*private DRSent drSent;
    private OpensDao opensDao;
    public void setOpensDao(OpensDao opensDao) {
        this.opensDao = opensDao;
    } 
    public OpensDao getOpensDao() {
        return this.opensDao;
    }
    
    private ClicksDao clicksDao;
    public void setClicksDao(ClicksDao clicksDao) {
        this.clicksDao = clicksDao;
    } 
    public ClicksDao getClicksDao() {
        return this.clicksDao;
    }*/
    private DRSentDao drSentDao;
    private DRSentDaoForDML drSentDaoForDML;
   
	public DRSentDaoForDML getDrSentDaoForDML() {
		return drSentDaoForDML;
	}
	public void setDrSentDaoForDML(DRSentDaoForDML drSentDaoForDML) {
		this.drSentDaoForDML = drSentDaoForDML;
	}
	public DRSentDao getDrSentDao() {
		return drSentDao;
	}
	public void setDrSentDao(DRSentDao drSentDao) {
		this.drSentDao = drSentDao;
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		 String requestedAction = request.getParameter("requestedAction");
		 String sentIdStr = request.getParameter(Constants.DR_SENTID);
			long sentId;
			try {
				sentId = Long.parseLong(sentIdStr);
			} catch (NumberFormatException e) {
				logger.error("** Exception : Invalid sentId for the requested action - "+requestedAction);
				return null;
			} 
		 
		 
		 if(requestedAction==null) {
	        	return null;
	        }
		 if (requestedAction.compareTo("open") == 0) {
	            openUpdate(request, response, sentId);
	        } else if(requestedAction.compareTo("click")== 0){
	        	clickUpdate(request,response,sentId);
	        }
		
		return null;
	}
	 public void openUpdate(HttpServletRequest request, HttpServletResponse response, long sentId)
				throws Exception {
				if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");       
				try {
				
				//DRSent drSent = drSentDao.findById(sentId);
				//updateOpens(request,drSent);
				//int  updateCount =drSentDao.updateOpenCount(sentId);
				int  updateCount =drSentDaoForDML.updateOpenCount(sentId);
				/*drSent.setOpens(updateCount);
				drSentDao.saveOrUpdate(drSent);*/
				
				} catch (Exception e) {
				logger.error("Exception : Problem while updating the opens \n", e);
				}
				
				try {
				response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
				response.setHeader("Pragma", "no-cache"); // HTTP 1.0
				response.setDateHeader("Expires", 0); // prevents caching at the proxy server
				response.setContentType("image/gif");
				File imgFile = new File(PropertyUtil.getPropertyValue("appDirectory") + "/img/digitransparent.gif");
				//logger.info("imgFil isexxist ::"+imgFile.exists()+" path is::"+imgFile.getPath());
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(imgFile));
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				try {
					byte b[] = new byte[8];
					int count;
					while((count=in.read(b)) != -1) {
						out.write(b,0,count);
					}
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}finally {
					out.flush();
					out.close();
					in.close();
				}
				
				
				/*File imgFile = new File(PropertyUtil.getPropertyValue("openImgUrl") + "/img/transparent.gif");
				
				logger.info("imgFil isexxist ::"+imgFile.exists()+" path is::"+imgFile.getPath());
				FileInputStream inputStream = new FileInputStream(imgFile);  //read the file
				
				 response.setHeader("Content-Disposition","attachment; filename=transparent.gif");
				 try {
				     int c;
				     while ((c = inputStream.read()) != -1) {
				     response.getWriter().write(c);
				     }
				 } catch (Exception e) {
				 	logger.error("Exception : Problem while sending the open image \n", e);
					}finally {
				     if (inputStream != null) {
				         inputStream.close();
				         response.getWriter().close();
				     }
				 }*/
				
				} catch (Exception e) {
				logger.error("Exception : Problem while sending the open image \n", e);
				}
				
				
				if(logger.isDebugEnabled()) logger.debug("-- Exit --");
				}
	 
	 public void clickUpdate(HttpServletRequest request, HttpServletResponse response, long sentId) {
	        if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");
	        DRSent drSent = drSentDao.findById(sentId);
	    
	        String urlStr;
	        urlStr = request.getParameter(Constants.DR_URL);
			try {
				urlStr = Utility.decodeUrl(urlStr);
				logger.debug("<<<<<<<<<<<<<<< url Str : " + urlStr);
				if(urlStr==null) {
					if(logger.isDebugEnabled()) logger.debug("URL is null");
					return;
				}
			} catch (EncoderException e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e1);
			} catch (DecoderException e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e1);
			} 
	        
	        /*if (urlStr.contains("http://www.loksatta.org/cms/")) {
	        	// TODO: temp code need to remove it.
	        	logger.debug("view : "+ request.getParameter("view"));
	        	urlStr = urlStr + "&view=" + request.getParameter("view") + "&id=" + request.getParameter("id");
	        }*/
	        
	        try {
	        	
	        	/** checking for opens, if zero it update*/ 
	        	if(drSent != null && drSent.getOpens() == 0) {
	        	//	updateOpens(request, drSent);
	        		//int  updateCount =drSentDao.updateOpenCount(sentId);
	        		int  updateCount =drSentDaoForDML.updateOpenCount(sentId);
	        		//int clickCount =drSentDao.updateClickCount(sentId);
	        		int clickCount =drSentDaoForDML.updateClickCount(sentId);
	        		/*updateOpens(request, drSent);
	        		updateReport(drSent, "opens");*/
	        	} 
	        	//int clickCount =drSentDao.updateClickCount(sentId);
	        	int clickCount =drSentDaoForDML.updateClickCount(sentId);
	        }

	        catch (Exception e) {
	        	logger.error("Exception : Problem while updating the Clicks \n", e);
			}
	        try {
	        	 if(urlStr.indexOf("http://") == -1 && (urlStr.indexOf("https://") == -1 )){
						urlStr = "http://" + urlStr.trim();
				}
				logger.debug("Redirecting to the URL:" + urlStr);
				response.sendRedirect(urlStr);
			} catch (IOException e) {
				logger.error("Exception : Problem while redirecting to the url : " + urlStr + "\n",e);
			}finally{
				logger.debug("---------contactScoreSetting update method is Called by clickUpdate---------------");
				
				/*contactScoreSetting.updateScore(campaignSent.getCampaignReport().getUser(), campaignSent.getEmailId(),
						Constants.SCORE_EMAIL_CLICK,campaignSent.getCampaignReport().getCampaignName());*/
				
			}
	        if(logger.isDebugEnabled()) logger.debug("-- Exit --");
	    }
	 
	 
	 
	


}



