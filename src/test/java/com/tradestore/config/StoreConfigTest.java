package com.tradestore.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.tradestore.dbstore.MongoStore;

@TestConfiguration
public class StoreConfigTest {
	
    
	@Bean
	public MongoStore mongoStore(MongoTemplate mongoTemplate) {
		return new MongoStore(mongoTemplate);
		
	}
}
