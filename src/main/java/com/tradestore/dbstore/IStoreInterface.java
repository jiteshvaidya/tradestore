package com.tradestore.dbstore;

import com.tradestore.domain.Trade;

public interface IStoreInterface {
	
	Integer getMaxVersion(String tradeId);
	void insertOrUpdateTrade(Trade trade);
	void insertTrade(Trade trade);
	long expireTradePastMaturity();

}
