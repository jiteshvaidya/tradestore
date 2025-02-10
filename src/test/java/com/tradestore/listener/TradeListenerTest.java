package com.tradestore.listener;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.tradestore.dbstore.IStoreInterface;
import com.tradestore.domain.Trade;

@SpringBootTest
@DirtiesContext
class TradeListenerTest {
	
	@Autowired
	TradeListener tradeListener;
	
	@Autowired
	@Qualifier("postgresStore")
	IStoreInterface storeInterface;
	
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		jdbcTemplate.execute("truncate table Trade");
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testListenTradeIdNull() {
		AssertionError assertionError = assertThrows(AssertionError.class, () -> tradeListener.listen(new Trade()));
		assertEquals("Trade ID can not be null", assertionError.getMessage());
	}
	@Test
	void testListenMaturityDateNull() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		AssertionError assertionError = assertThrows(AssertionError.class, () -> tradeListener.listen(trade));
		assertEquals("Maturity Date cannot be null", assertionError.getMessage());
	}
	@Test
	void testListenTradeIdWithSpecialChar() {
		Trade trade = new Trade();
		trade.setTradeId("fdd$@s");
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, +1);
		trade.setMaturityDate(instance.getTime());
		AssertionError assertionError = assertThrows(AssertionError.class, () -> tradeListener.listen(trade));
		assertEquals("Trade ID can only be aphanumeric", assertionError.getMessage());
	}

	@Test
	void testListenMaturityDateInThePastNull() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, -1);
		trade.setMaturityDate(instance.getTime());
		AssertionError assertionError = assertThrows(AssertionError.class, () -> tradeListener.listen(trade));
		assertEquals("Maturity Date should be greater than today", assertionError.getMessage());
	}

	@Test
	void testListenValid() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, 1);
		trade.setMaturityDate(instance.getTime());
		tradeListener.listen(trade);
		assertEquals(0, storeInterface.getMaxVersion("fdds"));
	}
	
	@Test
	void testListenMultipleVersion() {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, 1);
		Trade trade = new Trade();
		trade.setTradeId("tl-1");
		trade.setMaturityDate(instance.getTime());
		tradeListener.listen(trade);
		Trade trade2 = createTrade("tl-1", 1);
		trade.setMaturityDate(instance.getTime());
		
		
		tradeListener.listen(trade2);

		assertEquals(1, storeInterface.getMaxVersion("tl-1"));
	}

	@Test
	void testListenLowerVersion() {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, 1);
		Trade trade = createTrade("tl-2", 1);
		trade.setMaturityDate(instance.getTime());
		tradeListener.listen(trade);
		Trade trade2 = createTrade("tl-2", 0);
		trade2.setMaturityDate(instance.getTime());
		RuntimeException assertThrows = assertThrows(RuntimeException.class, () -> tradeListener.listen(trade2));
		assertEquals("Higher version already processed", assertThrows.getMessage());
	}
	
	
	@Test
	void testListenSameVersion() {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, 1);
		Trade trade = createTrade("tl-3", 1);
		trade.setMaturityDate(instance.getTime());
		tradeListener.listen(trade);
		Trade trade2 = createTrade("tl-3", 1);
		trade2.setMaturityDate(instance.getTime());
		tradeListener.listen(trade2);
		assertEquals(1, storeInterface.getMaxVersion("tl-3"));
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
