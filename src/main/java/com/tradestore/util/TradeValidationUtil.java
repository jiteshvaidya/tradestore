package com.tradestore.util;

import org.springframework.util.Assert;

import com.tradestore.domain.Trade;

public class TradeValidationUtil {
	
	public static void validateTrade(Trade trade) {
		Assert.isTrue(trade.getTradeId() != null, "Trade ID can not be null");
		Assert.isTrue(trade.getTradeId() != null,"Trade ID can not be null");
		Assert.isTrue(trade.getMaturityDate() != null , "Maturity Date cannot be null");
		Assert.isTrue(trade.getTradeId().matches("^[a-zA-Z0-9_-]*$") , "Trade ID can only be aphanumeric");
		Assert.isTrue(!trade.getMaturityDate().before(TradeStoreDateUtil.getTodaysDateWithoutTimePart()) , "Maturity Date should be greater than today");
	}
	

}
