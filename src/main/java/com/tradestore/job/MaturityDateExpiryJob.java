package com.tradestore.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tradestore.dbstore.IStoreInterface;

@Component
public class MaturityDateExpiryJob implements Job{
	private final Logger logger = LoggerFactory.getLogger(MaturityDateExpiryJob.class);

	@Autowired
	@Qualifier("mongoStore")
	private IStoreInterface iStoreInterface;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long startTime = System.nanoTime();
		logger.info("Expiry job started");
		long count = iStoreInterface.expireTradePastMaturity();
		long endTime = System.nanoTime();
		logger.info("Expiry job Completed. Trades Expired= {} time={}", count, (endTime - startTime)/1000000);
		
	}


}
