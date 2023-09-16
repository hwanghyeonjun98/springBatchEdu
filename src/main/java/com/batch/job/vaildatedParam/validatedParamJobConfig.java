package com.batch.job.vaildatedParam;

import com.batch.job.vaildatedParam.Vaildator.FileParamVaildate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class validatedParamJobConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job validatedParamJob(Step validatedParamStep) {
		return jobBuilderFactory
			.get("validatedParamJob")
			// .validator(new FileParamVaildate())
			.validator(multipleVaildator())
			.incrementer(new RunIdIncrementer())
			.start(validatedParamStep)
			.build();
	}
	
	private CompositeJobParametersValidator multipleVaildator() {
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
		validator.setValidators(Collections.singletonList(new FileParamVaildate()));
		
		return validator;
	}
	
	@JobScope
	@Bean
	public Step validatedParamStep(Tasklet validatedParamTasklet) {
		return stepBuilderFactory
			.get("validatedParamStep")
			.tasklet(validatedParamTasklet)
			.build();
	}
	
	@StepScope
	@Bean
	public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
		return (contribution, chunkContext) -> {
			System.out.println(fileName);
			System.out.println("Hello World Spring Batch");
			return RepeatStatus.FINISHED;
		};
	}
}
