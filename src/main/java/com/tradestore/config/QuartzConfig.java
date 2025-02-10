package com.tradestore.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tradestore.job.MaturityDateExpiryJob;

@Configuration
public class QuartzConfig {
	
	
	@Bean
	public JobDetail maturityDateExpiryJobDetail() {
		return JobBuilder.newJob(MaturityDateExpiryJob.class).withIdentity("MaturityDateExpiryJob").storeDurably().build();
	}
	@Bean
	public Trigger maturityDateExpiryJobTrigger() {
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 15 0 * * ?"); // every day at 00:15 am
		return TriggerBuilder.newTrigger().forJob(maturityDateExpiryJobDetail()).withIdentity("MaturityDateExpiryJobTrigger")
			.withSchedule(cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed()).build();
	}

}
