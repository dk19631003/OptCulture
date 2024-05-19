package com.optculture.launchpad.configs;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import com.optculture.shared.events.CommunicationEventHandler;
import com.optculture.launchpad.configs.CommunicationEventHandler;


@Configuration
public class MessagingConfigs {

	public static final String QUEUE = "communication_out_queue";
	public static final String EXCHANGE = "oc_exchange";
	public static final String ROUTING_KEY = "oc_key";


	public static final String EMPTYQUEUE = "empty_oc_queue";
	public static final String EMPTYEXCHANGE = "empty_oc_exchange";
	public static final String  EMPTYROUTING_KEY = "empty_oc_key";


	// Related to Communication_Event
	public static final String COMMUNICATION_EVENT_QUEUE = "communication_event_queue";
	public static final String COMMUNICATION_EVENT_EXCHANGE = "communication_event_exchange";
	public static final String COMMUNICATION_EVENT_ROUTING_KEY = "communication_event_key";

		//for couponIds transfer
	public static final String COUPON_QUEUE = "coupon_id_queue";
	public static final String COUPON_EXCHANGE = "coupon_id_exchange";
	public static final String COUPON_ROUTING_KEY = "coupon_id_key";
	@Bean
	public Queue emptyqueue() {
		return new Queue(EMPTYQUEUE);
	}

	@Bean
	public TopicExchange emptyexchange() {
		return new TopicExchange(EMPTYEXCHANGE);
	}

	@Bean
	public Binding emptybinding(Queue emptyqueue, TopicExchange emptyexchange) {
		return BindingBuilder.bind(emptyqueue).to(emptyexchange).with(EMPTYROUTING_KEY);
	}


	@Bean
	public Queue queue() {
		return new Queue(QUEUE);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(EXCHANGE);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
	}
	
	
	@Bean
	public Queue eventQueue() {
		return new Queue(COMMUNICATION_EVENT_QUEUE);
	}

	@Bean
	public TopicExchange eventExchange() {
		return new TopicExchange(COMMUNICATION_EVENT_EXCHANGE);
	}

	@Bean
	public Binding eventBinding(Queue eventQueue, TopicExchange eventExchange) {
		return BindingBuilder.bind(eventQueue).to(eventExchange).with(COMMUNICATION_EVENT_ROUTING_KEY);
	}

	@Bean
	public Queue couponQueue() {
		return new Queue(COUPON_QUEUE);
	}

	@Bean
	public TopicExchange couponExchange() {
		return new TopicExchange(COUPON_EXCHANGE);
	}

	@Bean
	public Binding couponBinding(Queue couponQueue, TopicExchange couponExchange) {
		return BindingBuilder.bind(couponQueue).to(couponExchange).with(COUPON_ROUTING_KEY);
	}

	@Bean
	public MessageConverter messageConverter() {
		return  new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate template(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter());
		return  template;
	}


	@Bean
	public CommunicationEventHandler communicationEventHandler() {
		return new CommunicationEventHandler();
	}



}
