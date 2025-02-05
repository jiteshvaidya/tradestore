package com.tradestore;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import com.tradestore.dbstore.IStoreInterface;
import com.tradestore.domain.Trade;
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class ApplicationTest {
	
	
	@Autowired
	private KafkaTemplate<Object, Object> template;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("postgresStore")
	IStoreInterface storeInterface;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		mongoTemplate.remove(new Query(), Trade.class);
		jdbcTemplate.execute("truncate table Trade");
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void IntegrationTest() {
		this.template.send("trade", createTrade("IT-1", 1));
		this.template.send("trade", createTrade("IT-1", 2));
		this.template.send("trade", createTrade("IT-1", 2));
		this.template.send("trade", createTrade("IT-2", 2));
		this.template.send("trade", createTrade("IT-2", 1));
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(2,  storeInterface.getMaxVersion("IT-1"));
		assertEquals(2,  storeInterface.getMaxVersion("IT-2"));
		
	}
	
	
	private Trade createTrade(String id, int version) {
		Trade t = new Trade();
		t.setTradeId(id);
		t.setVersion(version);
		t.setMaturityDate(new Date());
		t.setCreatedDate(new Date());
		t.setExpired(false);
		return t;
	}

}
