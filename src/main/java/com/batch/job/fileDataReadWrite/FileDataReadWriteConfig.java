package com.batch.job.fileDataReadWrite;

import com.batch.job.fileDataReadWrite.dto.Player;
import com.batch.job.fileDataReadWrite.dto.PlayerYears;
import com.batch.job.fileDataReadWrite.mapper.PlayerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@RequiredArgsConstructor
public class FileDataReadWriteConfig {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job fileDataReadWriterJob(Step fileReadWriterStep) {
		return jobBuilderFactory
			.get("fileDataReadWriterJob")
			.incrementer(new RunIdIncrementer())
			.start(fileReadWriterStep)
			.build();
	}
	
	@JobScope
	@Bean
	public Step fileReadWriterStep(
		ItemReader playerItemReader,
		ItemProcessor playerItemProcessor,
		ItemWriter playerFlatFileItemWriter
	) {
		return stepBuilderFactory
			.get("fileReadWriterStep")
			.<Player, PlayerYears>chunk(5)
			.reader(playerItemReader)
			// .writer(new ItemWriter() {
			// 	@Override
			// 	public void write(List items) throws Exception {
			// 		items.forEach(System.out::println);
			// 	}
			// })
			.processor(playerItemProcessor)
			.writer(playerFlatFileItemWriter)
			.build();
	}
	
	@StepScope
	@Bean
	public ItemProcessor<Player, PlayerYears> playerItemProcessor() {
		return new ItemProcessor<Player, PlayerYears>() {
			@Override
			public PlayerYears process(Player item) throws Exception {
				return new PlayerYears(item);
			}
		};
	}
	
	@StepScope
	@Bean
	public FlatFileItemWriter<Player> playerFlatFileItemWriter() {
		// 파일로 내보낼 필드 설정
		BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"});
		fieldExtractor.afterPropertiesSet();
		
		// 필드 구분자 설정 후 필드 set
		DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
		lineAggregator.setDelimiter(",");
		lineAggregator.setFieldExtractor(fieldExtractor);
		
		// 파일 내보내기
		FileSystemResource outoutResource = new FileSystemResource("players_output.txt");
		
		return new FlatFileItemWriterBuilder<PlayerYears>()
			.name("playerFlatFileItemWriter")
			.resource(outoutResource)
			.lineAggregator(lineAggregator) // 구분자
			.build();
	}
	
	@StepScope
	@Bean
	public FlatFileItemReader<Player> playerItemReader() {
		return new FlatFileItemReaderBuilder<Player>()
			.name("playerItemReader")
			.resource(new FileSystemResource("player.csv"))
			.lineTokenizer(new DelimitedLineTokenizer())
			.fieldSetMapper(new PlayerMapper())
			.linesToSkip(1)
			.build();
	}
}
