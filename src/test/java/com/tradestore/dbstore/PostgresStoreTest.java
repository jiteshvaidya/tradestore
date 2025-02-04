package com.tradestore.dbstore;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.kafka.test.context.EmbeddedKafka;

import com.tradestore.config.StoreConfigTest;
import com.tradestore.domain.Trade;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class PostgresStoreTest {
	
	@Autowired
	PostgresStore postgresStore;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@BeforeEach
	void clear() {
		jdbcTemplate.execute("truncate table Trade");
	}

	
	@Test
	void testGetMaxVersionInvalidTrade() {
		Integer maxVersion = postgresStore.getMaxVersion("test-t");
		assertNull(maxVersion);
	}

	@Test
	void testGetMaxVersionTradeMultipleVersion() {
		postgresStore.insertTrade(createTrade("test-t1", 1));
		postgresStore.insertTrade(createTrade("test-t1", 2));
		Integer maxVersion = postgresStore.getMaxVersion("test-t1");
		
		assertEquals(2, maxVersion.intValue());
	}
	
	
	@Test
	void testGetMaxVersionTradeSingleVersion() {
		postgresStore.insertTrade(createTrade("test-t2", 1));
		Integer maxVersion = postgresStore.getMaxVersion("test-t2");
		assertEquals(1, maxVersion.intValue());
	}
	
	@Test
	void testInsertTradeVersion1() {
		postgresStore.insertTrade(createTrade("test-t3", 1));
		Trade trade = findTrade("test-t3", 1);
		assertNotNull(trade);
	}
	
	@Test
	void testInsertTradeVersion2() {
		postgresStore.insertTrade(createTrade("test-t4", 1));
		postgresStore.insertTrade(createTrade("test-t4", 2));
		Trade trade = findTrade("test-t4", 2);
		assertNotNull(trade);
		assertEquals(2, postgresStore.getMaxVersion("test-t4")); 
	}

    @Test
	void testInsertTradeDuplicateException() {
    	postgresStore.insertTrade(createTrade("test-t5", 1));
		Exception exception = assertThrows(DuplicateKeyException.class, () -> postgresStore.insertTrade(createTrade("test-t5", 1)));
	}

    
	@Test
	void testInsertOrUpdateTradeVersion1() {
		postgresStore.insertOrUpdateTrade(createTrade("test-t3", 1));
		Trade trade = findTrade("test-t3", 1);
		assertNotNull(trade);
	}
	
	@Test
	void testInsertOrUpdateTradeVersion2() {
		postgresStore.insertOrUpdateTrade(createTrade("test-t4", 1));
		postgresStore.insertOrUpdateTrade(createTrade("test-t4", 2));
		Trade trade = findTrade("test-t4", 2);
		assertNotNull(trade);
		assertEquals(2, postgresStore.getMaxVersion("test-t4")); 
	}

    @Test
	void testInsertOrUpdateTradeDuplicate() {
    	postgresStore.insertOrUpdateTrade(createTrade("test-t5", 1));
    	postgresStore.insertOrUpdateTrade(createTrade("test-t5", 1));
		Trade trade = findTrade("test-t5", 1);
		assertNotNull(trade);
		assertEquals(1, postgresStore.getMaxVersion("test-t5")); 

	}

    @Test
	void testExpireTradePastMaturityNone() {
    	
    	postgresStore.insertOrUpdateTrade(createTrade("test-t5", 1));
    	postgresStore.insertOrUpdateTrade(createTrade("test-t6", 1));
    	long expireTradePastMaturity = postgresStore.expireTradePastMaturity();
    	
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
    	
    	
    	postgresStore.insertOrUpdateTrade(trade2);
    	postgresStore.insertOrUpdateTrade(createTrade("test-t6", 1));
    	
    	long expireTradePastMaturity = postgresStore.expireTradePastMaturity();
    	
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

    	postgresStore.insertOrUpdateTrade(trade2);
    	postgresStore.insertOrUpdateTrade(trade3);
    	
    	long expireTradePastMaturity = postgresStore.expireTradePastMaturity();
    	
		assertEquals(2, expireTradePastMaturity);

		Trade trade = findTrade("test-t5", 1);
		assertEquals(true, trade.isExpired());
		assertEquals(true, findTrade("test-t6", 1).isExpired());

	}



	private Trade findTrade(String tradeId, int version ) {
		return jdbcTemplate.queryForObject("select * from Trade where TRADE_ID = ? and VERSION = ?", new RowMapper<Trade>() {

			@Override
			public Trade mapRow(ResultSet rs, int rowNum) throws SQLException {
				Trade t = new Trade();
				t.setTradeId(rs.getString("TRADE_ID"));
				t.setVersion(rs.getInt("VERSION"));
				t.setExpired(rs.getBoolean("EXPIRED"));
				return t;
			}} , tradeId, version);
		
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
