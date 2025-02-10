package com.tradestore.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.tradestore.dbstore.MongoStore;

@ConditionalOnProperty(name="store-type" ,havingValue = "no-sql")
@Configuration
@Import( {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class NoSqlConfig {

	@Bean
	@ConditionalOnProperty(name="store-type", havingValue = "no-sql" )
	public MongoStore mongoStore(MongoTemplate mongoTemplate) {
		return new MongoStore(mongoTemplate);
		
	}

}
