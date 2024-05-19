package org.mq.loyality.utils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;


@Service
public class DefaultPageNavigator {
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
    public String buildPageNav(String path, int resultSize, int page, int pageSize, int pageNavTrail) {
        int allPage = resultSize / pageSize;

        boolean isOdd = (resultSize % pageSize != 0);
        allPage = (isOdd ? allPage+1 : allPage);

        int iStart = page - pageNavTrail;
        int iEnd = page + pageNavTrail;

        if(iStart < 1) {
            iEnd = iEnd + (1-iStart);
            iStart = 1;

            if(iEnd > allPage) {
                iEnd = allPage;
            }
        } else if(iEnd > allPage) {
            iStart = iStart - (iEnd - allPage);
            iEnd = allPage;

            if(iStart < 1) {
                iStart = 1;
            }
        }

        StringBuffer sb = new StringBuffer();
        int prev = page-1;
       if(prev == 0) {
    	   sb.append("<img src=\"resources/images/left_arrow.png\" border=\"0\" >").append(" ");
       }else{
    	   //sb.append("<a href='").append(path).append(prev-1).append("'>").append("<img src=\"resources/images/left_arrow.png\" border=\"0\" class=\"imageNoStyle\" />").append("</a>").append(" ");
    	   sb.append("<img src=\"resources/images/left_arrow.png\" border=\"0\" id=\"prevId\" style=\"cursor:pointer\" value='"+prev+"' />").append(" ");
       }
        for(int i=iStart; i <=iEnd; i++) {
            if(i == page) {
                sb.append("<span>").append(page).append("</span>").append(" ");
            }
            else {
                sb.append("<a href='").append(path).append(i).append("'>").append(i).append("</a>").append(" ");
            }
        }
       
        if(page == allPage){
        	 sb.append("<img src=\"resources/images/rightt_arrow.png\" border=\"0\" >").append(" ");
        }else{
        	prev +=2;
        	//sb.append("<a href='").append(path).append(page+1).append("'>").append("<img src=\"resources/images/rightt_arrow.png\" border=\"0\" class=\"imageNoStyle\" />").append("</a>").append(" ");
        	sb.append("<img src=\"resources/images/rightt_arrow.png\" border=\"0\" id=\"nextId\" style=\"cursor:pointer\" value='"+prev+"' />").append(" ");
        }
        logger.info(" jhgvb  "+sb.toString());
        return sb.toString();
    }
}
