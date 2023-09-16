package com.batch.job.jobListner;

import com.batch.job.jobListner.listner.JobLoggerListner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JobListenerJob {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job jobListnerJob(Step jobListenerStep) {
		return jobBuilderFactory
			.get("jobListnerJob")
			.incrementer(new RunIdIncrementer())
			.listener(new JobLoggerListner())
			.start(jobListenerStep)
			.build();
	}
	
	@JobScope
	@Bean
	public Step jobListenerStep(Tasklet jobListnerTasklet) {
		return stepBuilderFactory
			.get("jobListenerStep")
			.tasklet(jobListnerTasklet)
			.build();
	}
	
	@StepScope
	@Bean
	public Tasklet jobListnerTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				log.info("Job Listner!!!!");
				return RepeatStatus.FINISHED;
			}
		};
	}
}
