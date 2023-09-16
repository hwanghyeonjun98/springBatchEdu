package com.batch.job.dbDataReadWrite;

import com.batch.core.domain.account.Accounts;
import com.batch.core.domain.account.AccountsRepositoy;
import com.batch.core.domain.order.Orders;
import com.batch.core.domain.order.OrdersRepository;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * 주문 테이블 -> 정산 테이블 이관
 */

@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final OrdersRepository ordersRepository;
	private final AccountsRepositoy accountsRepositoy;
	
	@Bean
	public Job trMigration(Step trMigrationStep) {
		return jobBuilderFactory
			.get("trMigration")
			.incrementer(new RunIdIncrementer())
			.start(trMigrationStep)
			.build();
	}
	
	/**
	 * <Read data, Write Date>chunk(int)
	 * 어떤 데이터로 불러와서 chunk 설정 된 단위(수) 많큼 어떤 데이터로 처리 후 데이터를 커밋
	 * (chunk에서 설정된 단위로 트랜젝션 처리)
	 */
	@JobScope
	@Bean
	public Step trMigrationStep(
		ItemReader trOrderReader,
		ItemProcessor trOrderProcess,
		ItemWriter trAccountWriter
	) {
		return stepBuilderFactory
			.get("trMigrationStep")
			.<Orders, Orders>chunk(5)
			.reader(trOrderReader)
			// .writer(new ItemWriter() {
			// 	@Override
			// 	public void write(List items) throws Exception {
			// 		items.forEach(System.out::println);
			// 	}
			// })
			.processor(trOrderProcess)
			.writer(trAccountWriter)
			.build();
	}
	
	@StepScope
	@Bean
	public ItemProcessor<Orders, Accounts> trOrderProcess() {
		return new ItemProcessor<Orders, Accounts>() {
			@Override
			public Accounts process(Orders item) throws Exception {
				return new Accounts(item);
			}
		};
	}
	
	@StepScope
	@Bean
	public ItemWriter<Accounts> trAccountWriter() {
		return new RepositoryItemWriterBuilder<Accounts>()
			.repository(accountsRepositoy)// 사용할 레포지토리
			.methodName("save")
			.build();
	}
	
	@StepScope
	@Bean
	public RepositoryItemReader<Orders> trOrderReader() {
		return new RepositoryItemReaderBuilder<Orders>()
			.name("trOrderReader") // 메소드 이름
			.repository(ordersRepository) // 사용할 repository
			.methodName("findAll") // jpa 에소드 이름
			.pageSize(5) // 불러올 데이터 수 (chunk 사이즈와 같은 사이즈)
			.arguments(Collections.emptyList()) // 파라미터가 있을 시 arguments에 List로 설정
			.sorts(Collections.singletonMap("id", ASC)) // 데이터 정렬
			.build();
	}
}
