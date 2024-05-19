package com.optculture.launchpad.scheduler;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.optculture.launchpad.repositories.ScheduleRepository;
import com.optculture.launchpad.submitter.Submitter;
import com.optculture.shared.entities.communication.Schedule;

@Component
public class Scheduler {

	Logger logger = LoggerFactory.getLogger(Scheduler.class);

	@Autowired
	ScheduleRepository scheduleRepository;
	
	@Autowired
	Submitter submitter;

	@Scheduled(cron = "0 */2 * * * *") //runs every specified minute

	@org.springframework.transaction.annotation.Transactional
	public void runTask() {

		logger.info(" >>>>>>>>>>>>>> Started Scheduler :: run  <<<<<<<<<<<<<< ");

//		Calendar currentDate=Calendar.getInstance();
		LocalDateTime currentDate=LocalDateTime.now();
		List<Schedule> inactiveScheduleList = scheduleRepository.getInActiveList(currentDate);
		
		if(inactiveScheduleList!=null && !inactiveScheduleList.isEmpty()){

			for (Schedule schedule : inactiveScheduleList) {
				schedule.setStatus((byte) 9);
				scheduleRepository.save(schedule);

			}
		}

		//Change status of schedules that are active since 9 hrs

		//NOTE: DATE_ADD failed, hence the current date less 9 hours is passed.
		LocalDateTime currentDateMinus9Hrs=LocalDateTime.now();//.getTime();
		currentDateMinus9Hrs=currentDateMinus9Hrs.minusHours(9);


		 List<Schedule> activeForLongScheduleList = scheduleRepository.getActiveForLongList(currentDateMinus9Hrs);

		if(activeForLongScheduleList!=null && !activeForLongScheduleList.isEmpty()){

			for (Schedule schedule : activeForLongScheduleList) {

				schedule.setStatus((byte) 9);
				logger.info("made to expire "+schedule.getCsId());
				scheduleRepository.save(schedule);

			} 
		}




		List<Schedule> activeScheduleList = scheduleRepository.getActiveList(currentDate);



		if(activeScheduleList != null && !activeScheduleList.isEmpty()) {

			logger.info("activeScheduleList size ="+activeScheduleList.size());

			for(Schedule activeScheduleObj : activeScheduleList ) {

				try {
					logger.info(" scheduleId that is being added to rabbitMQ : "+activeScheduleObj.getCsId());

					/*
					 * customCommunication = new
					 * CustomCommunication(activeScheduleObj.getCommunication().getCommunicationId()
					 * ,);
					 * 
					 * //this will publish the obj to rabbitMQ
					 * template.convertAndSend(MessagingConfigs.EXCHANGE,
					 * MessagingConfigs.ROUTING_KEY, customCommunication);
					 */
					//call submitter to further processing.
					

					
					submitter.startProcessingCommunication(activeScheduleObj.getCommunication(),activeScheduleObj);
					
					//once it is published to rabbitMQ, make status=1
				//	activeScheduleObj.setStatus((byte)1);
				//	scheduleRepository.save(activeScheduleObj);

				} catch (AmqpException e) {
					logger.error("Exception :",e);
				}

			}
		}
	}
}
