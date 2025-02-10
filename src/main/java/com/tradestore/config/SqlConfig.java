package com.tradestore.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tradestore.dbstore.PostgresStore;


@ConditionalOnProperty(name="store-type" ,havingValue = "sql" , matchIfMissing = true )
@Configuration
@Import({DataSourceAutoConfiguration.class, 
    DataSourceTransactionManagerAutoConfiguration.class})
public class SqlConfig {
	
	@Bean
	@ConditionalOnProperty(name="store-type" ,havingValue = "sql", matchIfMissing = true )
	public PostgresStore postgresStore(JdbcTemplate jdbcTemplate) {
		return new PostgresStore(jdbcTemplate);
		
	}
	
}
