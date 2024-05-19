  <%@ page import=" org.springframework.security.core.Authentication"%>
  <%@ page import="org.mq.loyality.common.hbmbean.ContactsLoyalty"%>
  <%@page import="org.springframework.security.core.context.SecurityContextHolder" %>
  <%@page import=" java.text.DecimalFormat" %>
  
  <div class="navbar navbar-default" role="navigation">
        <div class="navbar-inner">
            <button type="button" class="navbar-toggle pull-left animated flip">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            
            <%String filePath1=(String)session.getAttribute("filePath"); %>
            <a class="navbar-brand" href="membership"> 
            <img alt="Sure" src="<%=filePath1 %>" width="113" height="59"  style="max-height:59px"  />
              </a>
              
             <%
     		ContactsLoyalty contact1 = (ContactsLoyalty) session.getAttribute("loyalityConfig");
      		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
      	    
      	     long points=0l;
      	     if(contact1.getLoyaltyBalance()!=null)
      	     {
      	    	points=contact1.getLoyaltyBalance().longValue();
      	     }
      	   double cbal=0.00 ;
      	   if(contact1.getRewardFlag().equalsIgnoreCase("GL")){
      	   if(contact1.getGiftcardBalance()!=null && contact1.getGiftBalance() != null)
      	   {
      		   cbal=contact1.getGiftcardBalance()+contact1.getGiftBalance();
      	   }else if(contact1.getGiftcardBalance()!=null){
      		   cbal = contact1.getGiftcardBalance();
      	   }else if(contact1.getGiftBalance() != null){
      		   cbal = contact1.getGiftBalance();
      	   }
      	   }else{
      		if(contact1.getGiftcardBalance()!=null)
        	{
        	   cbal=contact1.getGiftcardBalance();
       	   }  
      	   }
      		%>
  
    <div class="side_text">
        <span class="pull-right"><a href="#">
          Points Balance   : <%= points%> Points </a> <span class="pipe"> | </span> <a href="#"> Currency Balance   :  <%=decimalFormat.format(cbal) %> 
        </a></span>
     </div>         
        </div>
    </div> 