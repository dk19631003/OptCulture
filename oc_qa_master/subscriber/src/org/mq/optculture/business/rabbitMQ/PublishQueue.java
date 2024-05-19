package org.mq.optculture.business.rabbitMQ;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;


public class PublishQueue {
	
	private static ChannelPool channelPool;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public static void publish(String queueName,String exchangeName, Map<String, Object> bindingHeaders, String responseJson) {
		
		Channel channel = null;
		try {
			if(channelPool == null) {
				logger.info("new channel");
				channelPool = new ChannelPool();
			}
			logger.info("channel in queue "+channelPool);
			try {
				channel = channelPool.getChannel();
				// Declare the queue
			    channel.queueDeclare(queueName, true, false, false, null);
			    
			    // Declare the exchange
		        channel.exchangeDeclare(exchangeName, "headers", true);
		        
		        // Bind the queue to the exchange
		        channel.queueBind(queueName, exchangeName, "",bindingHeaders);
		        
		        // Publish the message with matching headers
	            AMQP.BasicProperties.Builder propertiesBuilder = new AMQP.BasicProperties.Builder();
	            propertiesBuilder.headers(bindingHeaders); // Add headers to the message
	            
	            //String responseJson = new Gson().toJson(data, EventPayload.class);
	            
			    channel.basicPublish(exchangeName, "", false, propertiesBuilder.build(), responseJson.getBytes(StandardCharsets.UTF_8));
			    
			    logger.info(" [x] Sent '" + responseJson + "'");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				if(channel !=null) {					
					channelPool.releaseChannel(channel);
					logger.info("channel released"+channel);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} // Create a pool with 5 channels
	}

}
