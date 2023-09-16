package com.batch.job.jobListner.listner;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggerListner implements JobExecutionListener {
	private final static String BEFORE_MSG = "{} Job is Running";
	private final static String AFFTER_MAG = "{} Job is Done. {Status: {}}";
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info(BEFORE_MSG, jobExecution.getJobInstance().getJobName());
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		log.info(AFFTER_MAG, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
		
		if(jobExecution.getStatus() == BatchStatus.FAILED) {
			// email ...
			log.info("Job is Fiailed");
		}
	}
}
