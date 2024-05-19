package org.mq.optculture.business.rabbitMQ;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;

public class RabbitMQConnection {
	
	private static Connection connection;
	public static Connection getConnection() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("https://b-a98be163-c9a0-4d0f-bba1-d917c70ed03f.mq.us-east-2.amazonaws.com/");
		factory.setUsername("admin");
		factory.setPassword("Opt@1234");
		
		try {
			if(connection == null) {
				System.out.println("new connection");
				connection = factory.newConnection();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connection;
	}

}
