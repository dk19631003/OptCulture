<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page import="java.util.List" %>
<%@ page import="org.mq.loyality.utils.ActivityForm" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
</head>
<body>
<div>
<table width="100%" style="max-width:600px; background-color:#FFFFFF; border:1px solid #e2e2e2;" bgcolor="#FFFFFF" border="0" align="center" cellpadding="10" cellspacing="0">
  <tr>
    <td>
<table width="100%" style="max-width:560px; background-color:#FFFFFF;" bgcolor="#FFFFFF" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
    <%List<ActivityForm> act=(List<ActivityForm>)request.getAttribute("activity") ;
    String subTot=(String)request.getAttribute("subTot");
    String tax=(String)request.getAttribute("tax");
    String tot=(String)request.getAttribute("total");
    
    %>  
      <%StringBuffer address=new StringBuffer("");
      if(act.get(0).getAdd1()!=null)
    	  address.append(act.get(0).getAdd1());
      if(act.get(0).getAdd2()!=null){
    	  if(address.length()>0)address.append(",");
    	  address.append(act.get(0).getAdd2());
      }
    	  if(address.length()>0)address.append("<br/>");
    	  if(act.get(0).getZip()!=null)
    		  address.append(act.get(0).getZip());
    	  if(act.get(0).getZip()!=null && act.get(0).getAdd3() != null)
    		  address.append(",");
    	  if(act.get(0).getAdd3() != null)
    		  address.append(act.get(0).getAdd3());
    	  if(act.get(0).getZip()!=null || act.get(0).getAdd3() != null)address.append("<br/>");
    	  if(act.get(0).getPhoneNo() != null)  address.append(act.get(0).getPhoneNo()+"<br/>");
      %>
        <td width="58%" class="wrp" align="left" valign="top" style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px;"><%=act.get(0).getStoreName() %><br />
          <%=address%>
          <%=act.get(0).getEmail() %></td>
        <td width="42%" class="wrp" align="right" valign="top" style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px;">Receipt# :  <%=act.get(0).getRecieptNo() %><br />
          Date :  <%=act.get(0).getDate() %><br />
          Payment Method: <%=act.get(0).getType() %><br /></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td height="20">&nbsp;</td>
  </tr>
  <tr>
    <td><table width="100%" border="0" cellspacing="2" style="word-break:break-all; word-wrap:break-word;border-spacing: 2px;" cellpadding="3">
      <tr>
        <td height="35"  style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; background-color: #f2f2f2; padding-left: 15px;">Item#</td>
        <td style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; background-color: #f2f2f2; padding-left: 15px;">Item Description</td>
        <td style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; background-color: #f2f2f2; padding-left: 15px;">Quantity</td>
        <td style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; background-color: #f2f2f2; padding-left: 15px;">Price</td>
        <td style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; background-color: #f2f2f2; padding-left: 15px;">Ext. Price</td>
      </tr>
      <%for(ActivityForm a:act)
      {%>
      <tr>
        <td valign="top" style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;"><%=a.getItemSid() %></td>
        <td valign="top" style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;"><%=a.getDCS() %></td>
        <td valign="top" align="left"  style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;"><%=a.getQuantityLong()%></td>
        <td valign="top" align="left"  style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;"><%=a.getPriceString()%></td>
        <td valign="top"  align="left"   style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;"><%=a.getExtPriceString() %></td>
      </tr>
      <%} %>
      
      <tr><td colspan="5"  style="border-bottom:1px solid #e2e2e2;"></td></tr>
      <tr>
        <td colspan="3"  valign="top" style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px;">&nbsp;</td>
        <td valign="top" align="left"  style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;""><strong>Subtotal</strong></td>
        <td valign="top" align="left"   style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;""><strong> <%=subTot %></strong></td>
      </tr>
      <tr>
        <td colspan="3"  valign="top" style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px;">&nbsp;</td>
        <td valign="top" align="left"    style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;""><strong>Tax</strong></td>
        <td valign="top" align="left"   style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;""  ><strong><%=tax %></strong></td>
      </tr>
      <tr>
        <td colspan="3" valign="top" style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px;">&nbsp;</td>
        <td valign="top" align="left"    style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;""><strong>Total   </strong></td>
        <td valign="top" align="left"  style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px; padding-left: 15px;""><strong><%=tot%></strong></td>
      </tr>
 
    </table></td>
  </tr>
  <tr>
    <td style="border-bottom:1px solid #e2e2e2;">&nbsp;</td>
  </tr>
  <tr>
    <td><table width="100%" border="0" cellspacing="3" cellpadding="1">
      <tr>
        <td>&nbsp;</td>
        <td align="right" valign="middle" style="font-family:'Segoe UI', Arial, Verdana; color:#333333; font-size:12px;">Receipt Total : <%=tot%></td>
      </tr>
      
      
      
      <!--##BEGIN PAYMENTS##-->
      <!--##END PAYMENTS##-->
    </table></td>
    </tr>
    <tr><td height="35"></td>
  </tr>
</table>
</td>
</tr>
</table>
</div>
</body>
</html>
