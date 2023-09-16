package com.batch.core.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SampleScheduler {
	@Autowired
	private Job helloWorldJob;
	@Autowired
	private JobLauncher jobLauncher;
	
	/**
	 * cron 표현식
	 * 초 - 분 - 시 - 날짜(1, 2, 3) - 월 - 요일
	 * * : 모든 수 또는 날짜
	 * / : 반복 설정
	 */
	@Scheduled(cron = "*/10 * * * * *")
	public void helloWorldJobRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		JobParameters jobParameters = new JobParameters(Collections.singletonMap("requestTime", new JobParameter(System.currentTimeMillis())));
		
		jobLauncher.run(helloWorldJob, jobParameters);
	}
}
