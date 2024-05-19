package com.optculture.launchpad.dispatchers;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

import com.optculture.shared.entities.communication.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.optculture.launchpad.repositories.CommunicationTemplateRepository;
import com.optculture.shared.entities.contact.Contact;
//import com.optculture.shared.events.CommunicationEventHandler;
import com.optculture.launchpad.configs.CommunicationEventHandler;
import com.optculture.shared.util.Constants;


@Component("MOCKGUPSHUP")
public class MockGupshupDispatcher implements ChannelDispatcher {

    @Autowired
    CommunicationEventHandler communicationEventHandler;

    Logger logger = LoggerFactory.getLogger(GupshupDispatcher.class);

    @Autowired
    CommunicationTemplateRepository communicationTemplateRepository ;

    private String apiEndPoint;
    private String userid ;
    private String pwd ;



    @Override
    public void dispatch(String msgContent, Contact contactobj, ChannelAccount channelAccount,
                         ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj, CustomCommunication in)
            throws IOException {

        try {

            logger.debug("----------< Entered MockGupshup Dispatcher >----------");

            //			String mobile = Utility.phoneParse(mobileNumber.trim(), user.getUserOrganization());
            //			String countryCarrier = user.getCountryCarrier() + Constants.STRING_NILL;
            //			if (mobile.startsWith(countryCarrier) && mobile.length() > user.getUserOrganization().getMinNumberOfDigits()) {
            //
            //				mobile = mobile.substring(countryCarrier.length());// needed for CM
            //
            //			}

            //String mobile = contactobj.getMobilePhone();
            logger.info("final  message content "+msgContent);

            this.apiEndPoint = channelSetting.getEndPoint(); //https://media.smsgupshup.com/GatewayAPI/rest
            //this.accessToken = channelAccount.getApiKey();
            this.userid = channelAccount.getAccountName();//2000210942
            this.pwd = channelAccount.getAccountPwd();

            CommunicationEvent communicationEvent = null;
            CommunicationTemplate commTemplate = null;
            String footer = null;
            String msg_type = null;
            String header_text = null;
            String media_url = null;

            commTemplate = communicationTemplateRepository.findByTemplateId(communicationObj.getTemplateId());

            if(commTemplate != null) {
                footer = commTemplate.getFooter();
                msg_type = commTemplate.getMsgType();

                if(msg_type!=null && msg_type.equals("TEXT")) {
                    header_text = commTemplate.getHeaderText();//if TEXT then keep static ie template level

                }else {//IMAGE/VIDEO/DOCUMENT
                    media_url = communicationObj.getMediaUrl();//url at communication level
                }

            }else {
                logger.debug("No Template Configured for the Communication");
                return;
            }

            URI uri = new URIBuilder(this.apiEndPoint)
                    .addParameter("userid", this.userid)
                    .addParameter("password", this.pwd)
                    .addParameter("send_to", contactobj.getMobilePhone())
                    .addParameter("v", "1.1")
                    .addParameter("format", "json")
                    .addParameter("msg_type", msg_type)//TEXT,IMAGE,VIDEO,DOCUMENT
                    .addParameter("isTemplate", "true")
                    .addParameter("auth_scheme", "PLAIN")
                    .addParameter("extra", in.getCommReportId().toString()) // pass the cr_id here to track the delivery reports
                    .build();

            if(msg_type!=null && msg_type.equals("TEXT")) {
                uri = new URIBuilder(uri)
                        .addParameter("msg", msgContent)
                        .addParameter("method", "SENDMESSAGE")
                        .build();

            }else{//IMAGE,DOCUMENT,VIDEO
                uri = new URIBuilder(uri)
                        .addParameter("caption", msgContent)
                        .addParameter("method", "SENDMEDIAMESSAGE")
                        .addParameter("media_url", media_url)
                        .build();
            }

            if(header_text!=null && !header_text.isEmpty()) {
                uri = new URIBuilder(uri)
                        .addParameter("header", header_text)
                        .build();
            }

            if(footer!=null && !footer.isEmpty()) {
                uri = new URIBuilder(uri)
                        .addParameter("footer", footer)
                        .build();
            }

            logger.debug("Final URL is ::\n"+uri);

            HttpClient httpClient = HttpClients.createDefault();

//            HttpGet httpGet = new HttpGet(uri);

//            HttpResponse response =

            int statusCode = 200;
            logger.debug("Response Status Code: " + statusCode);

//            String responseBody = EntityUtils.toString(response.getEntity());
//            logger.debug("Response Body: " + responseBody);

            String status = null;
            status = (statusCode == 200) ? "Sent" : "UnSent";

            //create the event object and Publish to rabbitMQ
            communicationEvent = new CommunicationEvent(in.getCommReportId(),in.getCampId(),contactobj.getMobilePhone(),status,LocalDateTime.now(),
                    Constants.STRING_NILL,communicationObj.getChannelType(),contactobj.getUserId(),contactobj.getContactId());

            communicationEventHandler.createCommunicationEvent(communicationEvent);

            logger.info("Completed mockgupshup dispatcher for contact :"+contactobj.getContactId());

        } catch (Exception e) {
            logger.error("Exception :: ", e);
        }


    }

}
