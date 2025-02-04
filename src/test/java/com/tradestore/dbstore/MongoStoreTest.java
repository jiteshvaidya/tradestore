package com.tradestore.dbstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.test.context.EmbeddedKafka;

import com.tradestore.domain.Trade;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class MongoStoreTest {
	

	@Autowired
	MongoStore mongoStore;
	
	@Autowired
	MongoTemplate mongoTemplate;

	
	@BeforeEach
	void clear() {
		mongoTemplate.remove(new Query(), Trade.class);
	}
	
	@Test
	void testGetMaxVersionInvalidTrade() {
		Integer maxVersion = mongoStore.getMaxVersion("test-t");
		assertNull(maxVersion);
	}

	@Test
	void testGetMaxVersionTradeMultipleVersion() {
		mongoStore.insertTrade(createTrade("test-t1", 1));
		mongoStore.insertTrade(createTrade("test-t1", 2));
		Integer maxVersion = mongoStore.getMaxVersion("test-t1");
		
		assertEquals(2, maxVersion.intValue());
	}
	
	
	@Test
	void testGetMaxVersionTradeSingleVersion() {
		mongoStore.insertTrade(createTrade("test-t2", 1));
		Integer maxVersion = mongoStore.getMaxVersion("test-t2");
		assertEquals(1, maxVersion.intValue());
	}
	
	
	@Test
	void testInsertTradeVersion1() {
		mongoStore.insertTrade(createTrade("test-t3", 1));
		Trade trade = findTrade("test-t3", 1);
		assertNotNull(trade);
	}
	
	@Test
	void testInsertTradeVersion2() {
		mongoStore.insertTrade(createTrade("test-t4", 1));
		mongoStore.insertTrade(createTrade("test-t4", 2));
		Trade trade = findTrade("test-t4", 2);
		assertNotNull(trade);
		assertEquals(2, mongoStore.getMaxVersion("test-t4")); 
	}

    @Test
	void testInsertTradeDuplicateException() {
    	mongoStore.insertTrade(createTrade("test-t5", 1));
		Exception exception = assertThrows(DuplicateKeyException.class, () -> mongoStore.insertTrade(createTrade("test-t5", 1)));
	}

    
	@Test
	void testInsertOrUpdateTradeVersion1() {
		mongoStore.insertOrUpdateTrade(createTrade("test-t3", 1));
		Trade trade = findTrade("test-t3", 1);
		assertNotNull(trade);
	}
	
	@Test
	void testInsertOrUpdateTradeVersion2() {
		mongoStore.insertOrUpdateTrade(createTrade("test-t4", 1));
		mongoStore.insertOrUpdateTrade(createTrade("test-t4", 2));
		Trade trade = findTrade("test-t4", 2);
		assertNotNull(trade);
		assertEquals(2, mongoStore.getMaxVersion("test-t4")); 
	}

    @Test
	void testInsertOrUpdateTradeDuplicate() {
    	mongoStore.insertOrUpdateTrade(createTrade("test-t5", 1));
    	mongoStore.insertOrUpdateTrade(createTrade("test-t5", 1));
		Trade trade = findTrade("test-t5", 1);
		assertNotNull(trade);
		assertEquals(1, mongoStore.getMaxVersion("test-t5")); 

	}
    
    @Test
	void testExpireTradePastMaturityNone() {
    	
    	mongoStore.insertOrUpdateTrade(createTrade("test-t5", 1));
    	mongoStore.insertOrUpdateTrade(createTrade("test-t6", 1));
    	long expireTradePastMaturity = mongoStore.expireTradePastMaturity();
    	
		assertEquals(0, expireTradePastMaturity);

		assertEquals(false, findTrade("test-t5", 1).isExpired());
		assertEquals(false, findTrade("test-t6", 1).isExpired());

	}


    @Test
	void testExpireTradePastMaturityOne() {
    	Trade trade2 = createTrade("test-t5", 1);
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DAY_OF_MONTH, -1);
    	trade2.setMaturityDate(cal.getTime());
    	
    	
    	mongoStore.insertOrUpdateTrade(trade2);
    	mongoStore.insertOrUpdateTrade(createTrade("test-t6", 1));
    	
    	long expireTradePastMaturity = mongoStore.expireTradePastMaturity();
    	
		assertEquals(1, expireTradePastMaturity);

		Trade trade = findTrade("test-t5", 1);
		assertEquals(true, trade.isExpired());
		assertEquals(false, findTrade("test-t6", 1).isExpired());

	}
    
    @Test
	void testExpireTradePastMaturityTne() {
    	Trade trade2 = createTrade("test-t5", 1);
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DAY_OF_MONTH, -1);
    	trade2.setMaturityDate(cal.getTime());
    	Trade trade3 = createTrade("test-t6", 1);
    	trade3.setMaturityDate(cal.getTime());

    	mongoStore.insertOrUpdateTrade(trade2);
    	mongoStore.insertOrUpdateTrade(trade3);
    	
    	long expireTradePastMaturity = mongoStore.expireTradePastMaturity();
    	
		assertEquals(2, expireTradePastMaturity);

		Trade trade = findTrade("test-t5", 1);
		assertEquals(true, trade.isExpired());
		assertEquals(true, findTrade("test-t6", 1).isExpired());

	}


    
	private Trade findTrade(String tradeId, int version ) {
		return mongoTemplate.findOne(new Query(Criteria.where("tradeId").is(tradeId).and("version").is(version)) , Trade.class);
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
