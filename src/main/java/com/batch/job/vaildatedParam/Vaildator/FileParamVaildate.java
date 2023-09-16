package com.batch.job.vaildatedParam.Vaildator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class FileParamVaildate implements JobParametersValidator {
	
	@Override
	public void validate(JobParameters parameters) throws JobParametersInvalidException {
		String fileName = parameters.getString("fileName");
		
		if(!StringUtils.endsWithIgnoreCase(fileName, ".csv")) {
			throw new JobParametersInvalidException("CSV 파일이 아닙니다.");
		}
	}
}
