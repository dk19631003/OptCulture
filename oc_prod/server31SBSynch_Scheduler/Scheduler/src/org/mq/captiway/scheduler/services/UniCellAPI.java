package org.mq.captiway.scheduler.services;

import java.io.IOException;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bulatnig.smpp.pdu.Npi;
import org.bulatnig.smpp.pdu.Ton;
import org.mq.captiway.scheduler.utility.Constants;
import org.smpp.Data;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.Address;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.SubmitSM;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.UnbindResp;
import org.smpp.pdu.WrongLengthOfStringException;

public class UniCellAPI {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public static void main(String[] args) {

     	
     	Session sess = null;
     	TCPIPConnection connection = null;
     	try {
     		AddressRange addRange = new AddressRange();
     		addRange.setNpi((byte)1);
     		addRange.setTon((byte)1);
//     		addRange.setAddressRange("9*");
     		
     		
     		
     	    final BindRequest request = new BindTransciever();
     	    request.setSystemId("magpromo");
     	    request.setPassword("S)t3Y(y@");
     	    //request.setSystemType("magpromo");
     	    //request.setAddressRange(addRange);
     	    request.setInterfaceVersion((byte) 0x34);
     	      connection = 
     	        new TCPIPConnection("smpp1.unicel.in", 51612);
     	    connection.setReceiveTimeout(3000);
     	    sess = new Session(connection);
     	    logger.debug("Send bind request...");
     	    final BindResponse response = sess.bind(request);
     	    if(response.getCommandStatus() == Data.ESME_RALYBND) {
     	    	UnbindResp resp =  sess.unbind();
     	    	
     	    	logger.debug("response..."+resp.debugString());
     	    	
     	    }
     	    
     	    EnquireLink link = new EnquireLink();
     	    logger.debug("Bind response " +response.getCommandStatus()+sess.isBound()+ response.debugString()+(response.getCommandStatus() == Data.ESME_ROK) );
     	    //Thread.sleep(12000);
     	} catch (Exception ex){
            //Analyze what type of exception was
            if (ex instanceof IOException || ex instanceof SocketException){
                //IOException relate to the brokenpipe issue you are facing
                //you need to close existing sessions and connections
                //restablish session
                if (connection!=null){
                    try {
						connection.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception :::", e);
					}
                }
                //This is a recursive call, I encourage you to elaborate
                //a little bit this method implementing a counter so you
                //don't end up in an infinite loop
               
            } else {
                //LOG whatever other exception thrown
            	logger.error("in Exception...",ex);
            	
            } 
    }finally{
     		/*try {
     			logger.debug("closing the session");
					sess.close();
				} catch (WrongSessionStateException e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e);
				}*/
     	}
     	
     	
     	try {
     	    final SubmitSM request = new SubmitSM();
     	    
     	   // request.setServiceType("amith");
     	   // request.setSourceAddr(createAddress("OPTCLT"));
     	    request.setDestAddr("9052221449");
     	    request.setShortMessage("Test - Promotional SMS");
     	    request.setScheduleDeliveryTime("");
     	    request.setReplaceIfPresentFlag((byte) 0);
     	    request.setEsmClass((byte) 0);
     	    request.setProtocolId((byte) 0);
     	    request.setPriorityFlag((byte) 0);
     	    request.setRegisteredDelivery((byte) 0);
     	    request.setDataCoding((byte) 0);
     	    request.setSmDefaultMsgId((byte) 0);
     	    logger.debug("Request............." + request.debugString());
     	    final SubmitSMResp response = sess.submit(request);
     	    Thread.sleep(6000);
     	    logger.debug("Submit response " + response.debugString() + 
     	        ", message id " + response.getMessageId());
//     	    Thread.sleep(6000);
     	    UnbindResp resp = sess.unbind();
     	    logger.debug("UnBind Response..............." + resp.debugString());
     	} catch (Throwable e) {
     	    logger.error("Exception :::", e);   
     	}
     	

     	
     	
		
		
		
		
		
	}
	
	private static Address createAddress(String address) 
 		    throws WrongLengthOfStringException {
 			Address addressInst = new Address();
 			addressInst.setTon((byte) Ton.ALPHANUMERIC); // national ton
 			addressInst.setNpi((byte) Npi.UNKNOWN); // numeric plan indicator
 			addressInst.setAddress(address, Data.SM_ADDR_LEN);
 			return addressInst;
 		}
 			
}
