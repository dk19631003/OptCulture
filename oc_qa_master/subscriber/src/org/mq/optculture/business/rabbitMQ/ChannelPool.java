package org.mq.optculture.business.rabbitMQ;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ChannelPool {
	
	//private Connection connection;
    private final BlockingQueue<Channel> pool;

    public ChannelPool() throws IOException {
    	int poolSize = 1;
    	//RabbitMQConnection queueConnection = new RabbitMQConnection();
    	Connection connection  = RabbitMQConnection.getConnection();// creating and returning connection
        this.pool = new LinkedBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Channel channel = connection.createChannel();
            pool.add(channel);
        }
    }

    public Channel getChannel() throws InterruptedException {
        return pool.take();
    }

    public void releaseChannel(Channel channel) {
        pool.offer(channel);
    }

}
