package com.optculture.launchpad.scheduler;

import com.optculture.launchpad.repositories.CommunicationRepository;
import com.optculture.launchpad.repositories.ScheduleRepository;
import com.optculture.shared.entities.communication.Communication;
import com.optculture.shared.entities.communication.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Optional;


@Component
public class ScheduleCreator {
    Logger logger= LoggerFactory.getLogger(ScheduleCreator.class);
    @Autowired
    CommunicationRepository communicationRepo;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Value("${schedule_creator_time}")
    long no_of_minutes ;
    @Scheduled(cron = "0 */${schedule_creator_time} * * * *") // 15 mins
    private  void checkRecurringSchedule(){
        logger.info("Started Looking for Recurring Schedules  ...");
        //TODO status ?
        List<Communication> communicationList=communicationRepo.findByScheduleTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndScheduleTimeBetween("Recurring",LocalDateTime.now(),LocalDateTime.now(),LocalTime.now().minusMinutes(2*no_of_minutes),LocalTime.now().plusMinutes(no_of_minutes));

        for(Communication comm :communicationList){

            logger.info("Frequency of Schedule :"+ comm.getFrequencyType());
            LocalDateTime enddate=comm.getEndDate();
            LocalDateTime scheduledDate=LocalDateTime.of(LocalDate.now(),comm.getScheduleTime());
            if(scheduledDate.isAfter(enddate)){ //if scheduletime after end time
                continue;
            }
            if(comm.getFrequencyType().equals("DAILY")){
                createSchedule(comm);
            }
            else if(comm.getFrequencyType().equals("WEEKLY")){
                DayOfWeek dayOfWeek=comm.getStartDate().getDayOfWeek();
                DayOfWeek currentDay=LocalDate.now().getDayOfWeek();
                if(dayOfWeek.equals(currentDay)){
                    createSchedule(comm);
                }
                else  continue;
            }
            else if(comm.getFrequencyType().equals("MONTHLY")){
                int dayOfMonth=comm.getStartDate().getDayOfMonth();
                int currentDayOfMonth=LocalDateTime.now().getDayOfMonth();
                if(dayOfMonth==currentDayOfMonth){
                    createSchedule(comm);
                }
                else  continue;
            }
            else if(comm.getFrequencyType().equals("YEARLY")){
                LocalDate date=comm.getStartDate().toLocalDate();
                LocalDate currentDate=LocalDate.now();
                if((date.getMonthValue()==currentDate.getMonthValue()) && (date.getDayOfMonth()==currentDate.getDayOfMonth())){
                    createSchedule(comm);
                }
                else  continue;
            }
        }
    }
    void createSchedule(Communication comm){
        logger.info("Inside of Schedule creation block for commId :"+comm.getCommunicationId());

        LocalDateTime startTime= LocalDateTime.of(LocalDate.now(),LocalTime.MIDNIGHT);
        LocalDateTime endTime=LocalDateTime.of(LocalDate.now().plusDays(1),LocalTime.MIDNIGHT);
        List<Schedule> schedules= scheduleRepository.findByCommunicationCommunicationIdAndScheduledDateGreaterThanEqualAndScheduledDateLessThan(comm.getCommunicationId(),startTime,endTime);
        if(schedules.isEmpty()){
            logger.info("No schedules present ! , creating new Schedule ");
            Schedule schedule= new Schedule();
            schedule.setStatus((byte) 0);
            schedule.setUserId(comm.getUserId());
            schedule.setScheduledDate(LocalDateTime.of(LocalDate.now(),comm.getScheduleTime())); //set time from date and time
            schedule.setCommunication(comm);
            schedule.setChannelType(comm.getChannelType());
            scheduleRepository.save(schedule);
        }
        else {
            logger.info("Schedules exist looking for updates .");
            for(Schedule schedule:schedules){

                if(comm.getStartDate().isAfter(schedule.getScheduledDate())){ //if start date changes.
                    scheduleRepository.delete(schedule);
                }
                if(schedule.getStatus()==(byte)2) continue;
                else if(schedule.getStatus()==(byte)1) continue;
                //todo if wrong
                else if(schedule.getStatus()==(byte)0){
                    if(!(schedule.getScheduledDate().toLocalTime().equals(comm.getScheduleTime()))) { //if time not same set updated time.
                        logger.info(" Inside schedule updating block schedule Id:" + schedule.getCsId());
                        schedule.setScheduledDate(LocalDateTime.of(LocalDate.now(), comm.getScheduleTime()));
                        scheduleRepository.save(schedule);
                    }
                }
                else if(schedule.getStatus()==(byte)9) {
                    if (comm.getModifiedDate() != null &&
                        comm.getModifiedDate().toLocalDate().equals(LocalDate.now())) {
                        schedule.setStatus((byte) 0);
                        schedule.setScheduledDate(LocalDateTime.of(LocalDate.now(),comm.getScheduleTime()));
                        scheduleRepository.save(schedule);
                    }
                }
            }
        }
    }
}
