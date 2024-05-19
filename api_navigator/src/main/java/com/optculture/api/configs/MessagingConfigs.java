package com.optculture.api.configs;

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


@Configuration
public class MessagingConfigs {

	// Related to Communication_Event
	public static final String COMMUNICATION_EVENT_QUEUE = "communication_event_queue";
	public static final String COMMUNICATION_EVENT_EXCHANGE = "communication_event_exchange";
	public static final String COMMUNICATION_EVENT_ROUTING_KEY = "communication_event_key";

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
	public MessageConverter messageConverter() {
		return  new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate template(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter());
		return  template;
	}

}
