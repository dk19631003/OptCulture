package com.optculture.launchpad.configs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.optculture.shared.entities.communication.CommunicationEvent;
import com.optculture.shared.segment.ClickHouseDB;

@Component
public class CommunicationEventHandler {

	private static Logger logger = LoggerFactory.getLogger(CommunicationEventHandler.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${clickhouse.url}")
	private String hostUrl;

	@Value("${clickhouse.username}")
	private String username;

	@Value("${clickhouse.password}")
	private String password;

	@Value("${communication.event.batch.size}")
	private int batchSize;

	// Thread-safe queue to collect events
	private static BlockingQueue<CommunicationEvent> eventQueue = new ArrayBlockingQueue<>(10000);

	private static LocalDateTime lastBatchInsertTime;

	// JDBC query for batch insertion
	private static final String INSERT_SQL = "INSERT INTO communication_event VALUES (?,?,?,?,?,?,?,?,?)";

	@Scheduled(cron = "0 */5 * * * *")
	public void run() {
		logger.info(">>>>>>>>>>>>>> Started EVENT Scheduler :: run <<<<<<<<<<<<<<");

		lastBatchInsertTime = lastBatchInsertTime == null ? LocalDateTime.now() : lastBatchInsertTime;
		Duration duration = Duration.between(lastBatchInsertTime, LocalDateTime.now());

		if (duration.toMinutes() >= 5 && !eventQueue.isEmpty()) {
			logger.info("**** EVENT-BATCH-START ****");

			batchInsertEvents();

			logger.info("**** EVENT-BATCH-END ****");

		}
	}

	public void createCommunicationEvent(CommunicationEvent ce) {

		rabbitTemplate.convertAndSend("communication_event_exchange", "communication_event_key", ce);

	}

	@RabbitListener(queues = "communication_event_queue", concurrency = "${COMM_EVENT_CONCURRENCY}")
	public void communicationEventListener(CommunicationEvent ce) {

		try {
			eventQueue.put(ce);
			logger.info("EVENT-QUEUE-SIZE :: {}", eventQueue.size());

			// Check if batch threshold is reached
			if (eventQueue.size() >= batchSize) {
				logger.info("**** START-EVENT-BATCH ****");

				batchInsertEvents();

				logger.info("**** END-EVENT-BATCH ****");
			}
		} catch (Exception e) {
			logger.error("Exception in Communication Events :: ", e);
		}
	}

	// Function to handle batch insertion into the database
	private synchronized void batchInsertEvents() {

		// Dequeue batch of events
		Queue<CommunicationEvent> batchEvents = new ArrayBlockingQueue<>(10000);
		logger.debug("Queue size before draining : "+eventQueue.size());
		eventQueue.drainTo(batchEvents);

		String[] dbConnectionInfo = { hostUrl, username, password };

		try (Connection conn = new ClickHouseDB().getClickHouseDBConnection(dbConnectionInfo);
				PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

			conn.setAutoCommit(false);

			// Iterate over events and add batch parameters
			for (CommunicationEvent ce : batchEvents) {
				ps.setLong(1, ce.getCrId());
				ps.setLong(2, ce.getCampaignId());
				ps.setString(3, ce.getRecipient());
				ps.setString(4, ce.getEventType());
				ps.setTimestamp(5, Timestamp.valueOf(ce.getEventDate().withSecond(0).withNano(0)));
				ps.setString(6, ce.getApiMsgId());
				ps.setString(7, ce.getChannelType());
				ps.setLong(8, ce.getUserId());
				ps.setLong(9, ce.getContactId());

				ps.addBatch();
			}

			// Execute batch insertion
			int[] insertCount = ps.executeBatch();
			logger.info("BATCH-INSERT-COUNT :: {}", insertCount.length);
			conn.commit();
		} catch (SQLException e) {
			logger.error("Exception :: ", e);
		}

		lastBatchInsertTime = LocalDateTime.now();
	}

}
