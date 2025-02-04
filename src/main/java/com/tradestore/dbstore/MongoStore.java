package com.tradestore.dbstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;
import com.tradestore.domain.Trade;
import com.tradestore.util.TradeStoreDateUtil;

public class MongoStore implements IStoreInterface{

	private MongoTemplate mongoTemplate;
	
	public MongoStore(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		this.mongoTemplate.indexOps(Trade.class).ensureIndex(new Index().on("tradeId", Direction.ASC).on("version", Direction.ASC).unique());
	}
	
	
	@Override
	public Integer getMaxVersion(String tradeId) {
		
		Trade trade = mongoTemplate.findOne(new Query(Criteria.where("tradeId").is(tradeId)).with(Sort.by("version").descending()), Trade.class);
		if (trade !=null) {
			return Integer.valueOf(trade.getVersion());
		}
		return null;
	}

	@Override
	public void insertOrUpdateTrade(Trade trade) {
		
		Query query = new Query(Criteria.where("tradeId").is(trade.getTradeId()).and("version").is(trade.getVersion()));
		Update update = new Update().set("tradeId", trade.getTradeId()).set("version", trade.getVersion()).set("counterPartyId", trade.getCounterPartyId()).set("bookId", trade.getBookId())
				.set("maturityDate", trade.getMaturityDate()).set("createdDate", trade.getCreatedDate()).set("expired", trade.isExpired());
		mongoTemplate.upsert(query, update, trade.getClass());
	}

	@Override
	public void insertTrade(Trade trade) {
		mongoTemplate.insert(trade);
	}


	@Override
	public long expireTradePastMaturity() {
		Query query = new Query(Criteria.where("maturityDate").lt(TradeStoreDateUtil.getTodaysDateWithoutTimePart()));
		Update update = new Update().set("expired", true);
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, Trade.class);
		return updateMulti.getModifiedCount();
	}



}
