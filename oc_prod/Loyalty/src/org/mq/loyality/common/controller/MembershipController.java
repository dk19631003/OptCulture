package org.mq.loyality.common.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.LoyaltyProgram;
import org.mq.loyality.common.hbmbean.LoyaltyProgramTier;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.common.service.MembershipService;
import org.mq.loyality.common.service.UserService;
import org.mq.loyality.exception.LoyaltyProgramException;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.MembershipDetails;
import org.mq.loyality.utils.MyCalendar;
import org.mq.loyality.utils.OCConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
public class MembershipController {
	@Autowired
	private MembershipService membershipService;
	@Autowired
	private UserService userService;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	public MembershipService getMembershipService() {
		return membershipService;
	}
	public void setMembershipService(MembershipService membershipService) {
		this.membershipService = membershipService;
	}

	@RequestMapping(value="/membership",method = RequestMethod.GET)
	public String getMemberShipDetails(ModelMap model,HttpServletRequest request)
	{
		String expDate =null;
		MembershipDetails memDetails=new MembershipDetails();
		//ContactsLoyalty contact = null;
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		HttpSession session=request.getSession();
		ContactsLoyalty contact = null;
		if(session.getAttribute("loyalityConfig")!=null)
		{
			contact = (ContactsLoyalty) session
					.getAttribute("loyalityConfig");
		}
		else
		{
			return "common/login-form";
		}
		String orgname;
		String name = request.getServerName();
		String name1 = "http://".concat(name);
		LoyaltySettings loyalitySettings = userService.getSettingDetails(name1);
		List<UserOrganization> userList = userService.getOrgDetails(loyalitySettings.getUserOrgId());
		if (userList.size() != 0) {
			UserOrganization user = userList.get(0);
			orgname = user.getOrganizationName();
		} else {
			orgname = "default";
		}
		String path=loyalitySettings.getPath();
		String folderpath="";
		String folder="";

		if(path!=null)
		{
			String split[]=path.split("/");
			name=split[8];
			folderpath=split[7];
			folder=split[6];
		}

		session.setAttribute("filePath",request.getContextPath()+"/"+folder+"/"+folderpath+"/"+name);
		session.setAttribute("orgname", orgname);
		session.setAttribute("colorCode",loyalitySettings.getColorCode());

		//membership no
		if(contact!=null && contact.getCardNumber()!=null){
			memDetails.setMemshipNo(contact.getCardNumber().toString());
		}else{
			memDetails.setMemshipNo("n/a");
		}
		//membership since
		memDetails.setMemshipSince(MyCalendar.calendarToString(contact.getCreatedDate(),org.mq.loyality.utils.MyCalendar.FORMAT_DATETIME_STDATE, null));
		Long tierId = contact.getProgramTierId();
		LoyaltyProgramTier lt=null;
		if(tierId !=null) lt=membershipService.getTierById(tierId);
		if(lt!=null){

			StringBuffer sb=new StringBuffer();
			if(contact.getHoldPointsBalance()!=null && contact.getHoldPointsBalance().longValue()!=0)
			{
				sb.append(contact.getHoldPointsBalance().longValue()+" Points");
			}

			if(contact.getHoldAmountBalance()!=null && contact.getHoldAmountBalance().longValue() != 0 )
			{
				if(sb.length() >0 )sb.append(" , ");
				sb.append(""+contact.getHoldAmountBalance().longValue());
			}

			/*if(lt.getActivationFlag()!='Y' && sb.toString().isEmpty() )
			{
				memDetails.setRewardsOnHold(null);

			}else */if(!sb.toString().isEmpty())
			{
				memDetails.setRewardsOnHold(sb.toString());
			}
			else if(sb.toString().isEmpty() && lt.getActivationFlag()=='Y')
			{
				memDetails.setRewardsOnHold("0 Points");
			}else 
			{
				memDetails.setRewardsOnHold(null);
			}

		}	

		//current points balance
		if(contact.getLoyaltyBalance()!=null){
			memDetails.setCurBal(contact.getLoyaltyBalance().longValue()+" Points");
		}

		else
		{
			memDetails.setCurBal("0");
		}
		//currency balance
		if(contact.getRewardFlag().equalsIgnoreCase("GL")){
		if(contact.getGiftcardBalance()!=null && contact.getGiftBalance() != null){
			memDetails.setCurrentCurrencyBal(""+decimalFormat.format(Double.valueOf(contact.getGiftcardBalance().toString()))+" Loyalty & "+""+decimalFormat.format(Double.valueOf(contact.getGiftBalance().toString()))+" Gift");
		}else if(contact.getGiftcardBalance()!=null ){
			memDetails.setCurrentCurrencyBal(""+decimalFormat.format(Double.valueOf(contact.getGiftcardBalance().toString()))+" Loyalty");
		}else if(contact.getGiftBalance() != null){
			memDetails.setCurrentCurrencyBal(""+decimalFormat.format(Double.valueOf(contact.getGiftBalance().toString()))+" Gift");
		}else{
			memDetails.setCurrentCurrencyBal("0.00");
		}
		}
		else
		{
			if(contact.getGiftcardBalance()!=null){
				memDetails.setCurrentCurrencyBal(""+decimalFormat.format(Double.valueOf(contact.getGiftcardBalance().toString())));
			}else{
				memDetails.setCurrentCurrencyBal("0.00");
			}
		}

		LoyaltyProgramTier loyaltyProgramTier = null;
		if(tierId != null)loyaltyProgramTier = lt;

		if(loyaltyProgramTier!=null){

			//current tier
			/*if(loyaltyProgramTier.getTierType()!=null){*/
				memDetails.setCurrentTier(loyaltyProgramTier.getTierName());
			/*}
			else
			{
				memDetails.setCurrentTier("--");
			}*/

			if(loyaltyProgramTier.getPtsActiveDateValue()!=null){
				memDetails.setRewardActivationPeriod(loyaltyProgramTier.getPtsActiveDateValue().toString()+" "+loyaltyProgramTier.getPtsActiveDateType()+"(s)");
			}else{
				memDetails.setRewardActivationPeriod("--");
			}
			LoyaltyProgram program = membershipService.findById(contact.getProgramId());
			if(program!=null){

				if(program.getTierEnableFlag()=='Y')
				{
					String pointsReqToUpgradeTier = "";
					Double points = null;
					try {
						points = membershipService.findPointsToUpgrade(contact, loyaltyProgramTier);
					} catch (LoyaltyProgramException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::: ",e);
					}
					if(loyaltyProgramTier.getTierUpgdConstraintValue() != null){
						if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(loyaltyProgramTier.getTierUpgdConstraint())){
							pointsReqToUpgradeTier = "Making Purchase Worth "+decimalFormat.format(points)+" in "+loyaltyProgramTier.getTierUpgradeCumulativeValue()+" Months";
							
						}else{
						if(points != null ){
							/*pointsReqToUpgradeTier = String.valueOf(new DecimalFormat("#").format(loyaltyProgramTier.getTierUpgdConstraintValue() - points))+" Points";*/
							points =  loyaltyProgramTier.getTierUpgdConstraintValue() - points;
						}else {
							/*pointsReqToUpgradeTier = String.valueOf(new DecimalFormat("#").format(loyaltyProgramTier.getTierUpgdConstraintValue()))+" Points";*/
							points = loyaltyProgramTier.getTierUpgdConstraintValue();
						}
						pointsReqToUpgradeTier = points > 0 ? new DecimalFormat("#").format(points):" ";
						pointsReqToUpgradeTier = !pointsReqToUpgradeTier.trim().isEmpty() ? OCConstants.LOYALTY_LIFETIME_POINTS.equals(loyaltyProgramTier.getTierUpgdConstraint()) ?"Earning "+ pointsReqToUpgradeTier+" Points of Total Points":" Making Purchase Worth "+pointsReqToUpgradeTier:" ";
					/*	
						pointsReqToUpgradeTier = points > 0 ? new DecimalFormat("#").format(points):"N/A";
						pointsReqToUpgradeTier = OCConstants.LOYALTY_LIFETIME_POINTS.equals(loyaltyProgramTier.getTierUpgdConstraint()) && pointsReqToUpgradeTier.equalsIgnoreCase("N/A") ?"Earning "+ pointsReqToUpgradeTier+" Points of Total Points":" Making Purchase Worth $ "+pointsReqToUpgradeTier;*/
						}
					}else {
						pointsReqToUpgradeTier ="N/A";
					}
					memDetails.setMemshipUpgradeOn(pointsReqToUpgradeTier);
				}
			}
				else
				{
					memDetails.setMemshipUpgradeOn("N/A");
				}

				if('Y' == program.getRewardExpiryFlag() && loyaltyProgramTier.getRewardExpiryDateType() != null 
						&& loyaltyProgramTier.getRewardExpiryDateValue() != null){

					Calendar cal = Calendar.getInstance();
					if(("Month").equals(loyaltyProgramTier.getRewardExpiryDateType())){
						cal.add(Calendar.MONTH, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
					}
					else if(("Year").equals(loyaltyProgramTier.getRewardExpiryDateType())){
						cal.add(Calendar.YEAR, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
					}

					expDate = "";
					if(cal.get(Calendar.MONTH) == 11) {
						expDate = cal.get(Calendar.YEAR)+"-12";
					} 
					else {
						cal.add(Calendar.MONTH, 1);
						expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
					}
					Object[] expiryValueArr=null;
					if(expDate!=null && contact.getRewardFlag()!=null)
					{
						expiryValueArr = fetchExpiryValues(contact.getLoyaltyId(), expDate, contact.getRewardFlag());
					}
					String rewardExpVal=null;
					if(expiryValueArr != null ) { 
						if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && expiryValueArr[2] != null
								&& Double.valueOf(expiryValueArr[2].toString()) >  0.0){
							rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points"+
									" & "+decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));

						}
						else if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && (expiryValueArr[2] == null ||
								Double.valueOf(expiryValueArr[2].toString()) == 0.0)) {
							rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points";
						}
						else if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) >  0.0
								&& (expiryValueArr[1] == null || Long.valueOf(expiryValueArr[1].toString()) == 0)){
							rewardExpVal = ""+decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
						}

					}
					memDetails.setRewardExpiringThisMonth(rewardExpVal != null ? rewardExpVal:"0 Points") ;
				}	
			}
		
		else
		{
			memDetails.setCurrentTier(null);
		}

		model.addAttribute("loyalitySettings", loyalitySettings);
		model.addAttribute("membership", memDetails);
		session.setAttribute("memDetails",memDetails);
		return "common/MembershipPage";
	}

	private  Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag)  {
		return membershipService.fetchExpiryValues(loyaltyId, expDate, rewardFlag);
	}

	public static String getAmountInUSFormat(Object numberToFormatted) {
		String usFormatValue ="$ ";
		try {
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
			if (numberFormat instanceof DecimalFormat) {
				DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
				decimalFormat.applyPattern("#0.00");
				decimalFormat.setGroupingUsed(true);
				decimalFormat.setGroupingSize(3);
				usFormatValue = usFormatValue + decimalFormat.format(numberToFormatted);
			}//if
		} catch (Exception e) {
			usFormatValue = usFormatValue + numberToFormatted;
		}
		return usFormatValue;
	}//getAmountInUSFormat
}
