package com.optculture.launchpad.configs;


import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.optculture.launchpad.dispatchers.ChannelDispatcher;
import com.optculture.launchpad.dispatchers.MailMergingProcessor;
//import com.optculture.shared.entities.communication.*;
import com.optculture.launchpad.repositories.*;
import com.optculture.shared.entities.communication.*;
import com.optculture.shared.entities.contact.Contact;

@Component
public class MessageListener {

	@Autowired
	ApplicationContext context;

	@Autowired
	ContactRepository contactRepository;
	@Autowired
	UserChannelSettingRepository userChannelSettingRepository;

	@Autowired
	CommunicationRepository communicationRepository;

	@Autowired
	ChannelAccountRepository channelAccountRepository;

	@Autowired
	ChannelSettingRepository channelSettingRepository;

	@Autowired
	MailMergingProcessor mailMergingProcessor;

	Logger logger = LoggerFactory.getLogger(MessageListener.class);

	//	@Transactional
	@RabbitListener(queues=MessagingConfigs.QUEUE, concurrency = "${rabbitmq.listener.concurrency}")
	public void listen(CustomCommunication in) throws IOException {
		try {
			logger.info("Received msg is :"+in);

			ChannelDispatcher dispatcher=null;

			Communication communicationObj = communicationRepository.findByCommunicationId(in.getCampId());

			Contact contactobj= contactRepository.findByUserIdAndContactId(communicationObj.getUserId(), in.getContactId());
			
			UserChannelSetting userChannelSettingObj = null;
			
			if(communicationObj.getSenderId()!=null && !communicationObj.getSenderId().isEmpty())
				userChannelSettingObj = userChannelSettingRepository.findByUserIdAndChannelTypeAndSenderId(communicationObj.getUserId(), communicationObj.getChannelType(),communicationObj.getSenderId());
			else
				userChannelSettingObj = userChannelSettingRepository.findByUserIdAndChannelType(communicationObj.getUserId(), communicationObj.getChannelType());

			Optional<ChannelAccount> channelAccountObj = null;
			
			if(userChannelSettingObj!=null) {
				channelAccountObj = channelAccountRepository.findById(userChannelSettingObj.getChannelAccount().getId());
			}else {
				logger.info("userChannelSettingObj is null");
				return;
			}

			Optional<ChannelSetting> channelSettingObj = channelSettingRepository.findById(channelAccountObj.get().getChannelSettings().getId());

			try {
				if(channelSettingObj.isPresent()) {
					//get the dispatcher i.e. Equence/ClickaTell/CM/Meta/Email dispatchers
					dispatcher = (ChannelDispatcher) context.getBean(channelSettingObj.get().getGatewayName());
					
					String finalMessageContent = null;
					finalMessageContent = dispatcher.beforeMailMerge(contactobj, communicationObj, channelAccountObj.get(),userChannelSettingObj, in);
					if(finalMessageContent != null)
						communicationObj.setMessageContent(finalMessageContent);
				}
			} catch (Exception e) {
				logger.info("Exception while fetching dispatcher ",e);
				throw new AmqpRejectAndDontRequeueException("Error processing message", e);

			}

			String finalContent =	mailMergingProcessor.getProcessedTemplate(communicationObj,contactobj);

			logger.info("mail merging completed for cid = "+contactobj.getContactId());

			String messageContent =  finalContent;
			
			if(!messageContent.isEmpty() && dispatcher != null) {

				try {
					//forward the final message to the corresponding dispatcher
					dispatcher.dispatch(messageContent, contactobj, channelAccountObj.get(), channelSettingObj.get(), userChannelSettingObj, communicationObj, in);

				} catch (Exception e) {
					logger.info("Exception while calling dispatcher ",e);
					throw new AmqpRejectAndDontRequeueException("Error processing message", e);

				}
			}// if content is empty
			else {
				logger.info("Message content is Empty from the free marker for communication :"+communicationObj.getName()+" ,contactId : "+in.getContactId());
			}
		}catch(Exception e) {
			logger.info("Something went wrong ... ",e);
			throw new AmqpRejectAndDontRequeueException("Error processing message", e);
		}
	}
}
