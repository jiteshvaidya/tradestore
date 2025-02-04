package com.tradestore.dbstore;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.tradestore.domain.Trade;
import com.tradestore.util.TradeStoreDateUtil;

public class PostgresStore implements IStoreInterface{
	
	private JdbcTemplate jdbcTemplate;
	
	public PostgresStore (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Override
	public Integer getMaxVersion(String tradeId) {
		
		return jdbcTemplate.queryForObject("Select max(version) VERSION from TRADE where TRADE_ID = ?", new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				// getObject because its possible that VERSION is null.
				return rs.getObject("VERSION", Integer.class);
			}
		} , tradeId);
		

	}

	@Override
	public void insertOrUpdateTrade(Trade trade) {
		// Tring update first as its more often likely to be successful . it will only fail once when inserting the 
		
		int updateCouny = jdbcTemplate.update("update TRADE set COUNTER_PARTY_ID = ? , BOOK_ID = ?, MATURITY_DATE =?, CREATED_DATE = ?, EXPIRED =?  where TRADE_ID = ? and VERSION =?", 
				trade.getCounterPartyId(), trade.getBookId(), trade.getMaturityDate(), trade.getCreatedDate(), trade.isExpired(), trade.getTradeId(), trade.getVersion()); 
		
		if (updateCouny ==0 ) {
			insertTrade(trade);
		}
		
		
	}

	@Override
	public void insertTrade(Trade trade) {
		jdbcTemplate.update("Insert into TRADE (TRADE_ID, VERSION, COUNTER_PARTY_ID, BOOK_ID, MATURITY_DATE, CREATED_DATE, EXPIRED) values (?,?,?,?,?, ?, ?)", 
				trade.getTradeId(), trade.getVersion(), trade.getCounterPartyId(), trade.getBookId(), trade.getMaturityDate(), trade.getCreatedDate(), trade.isExpired());		
	}

	@Override
	public long expireTradePastMaturity() {
		return jdbcTemplate.update("update  TRADE set EXPIRED =? where MATURITY_DATE < ?", Boolean.TRUE, TradeStoreDateUtil.getTodaysDateWithoutTimePart()); 
	}
	
	
}
