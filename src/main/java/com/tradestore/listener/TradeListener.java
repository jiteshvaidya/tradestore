package com.tradestore.listener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tradestore.dbstore.IStoreInterface;
import com.tradestore.domain.Trade;
import com.tradestore.util.TradeStoreDateUtil;

@Component
public class TradeListener {
	private final Logger logger = LoggerFactory.getLogger(TradeListener.class);
	
	@Autowired
	private IStoreInterface iStoreInterface;
	
	
	//TODO replace this by a cache so that are there are no concerns of map growing too big
	private final ConcurrentHashMap<String, ReentrantLock> tradeIdLockMap = new ConcurrentHashMap<String, ReentrantLock>();
	
	@KafkaListener(id = "tradestore", topics = "trade")
	@Transactional
	public void listen(Trade trade) {
		logger.info("Received: " + trade);
		assert trade.getTradeId() != null : "Trade ID can not be null";
		assert trade.getMaturityDate() != null : "Maturity Date cannot be null";
		assert !trade.getMaturityDate().before(TradeStoreDateUtil.getTodaysDateWithoutTimePart()) : "Maturity Date should be greater than today";
		if (trade.getCreatedDate() == null) {
			trade.setCreatedDate(TradeStoreDateUtil.getTodaysDateWithoutTimePart());
		}
		ReentrantLock lock = tradeIdLockMap.putIfAbsent(trade.getTradeId(), new ReentrantLock());
		lock = tradeIdLockMap.get(trade.getTradeId());
		lock.lock();
		try {
			Integer max = iStoreInterface.getMaxVersion(trade.getTradeId());
			if (max == null) {
				iStoreInterface.insertTrade(trade);
			}else {
				if (max.intValue() > trade.getVersion()) {
					logger.error("Higher version already processed. tradeId={} version={}", trade.getTradeId() , trade.getVersion());
					throw new RuntimeException("Higher version already processed");
				}
				else {
					// insert of update since it possible to have multiple updates with same version
					iStoreInterface.insertOrUpdateTrade(trade);    
				}
			}
		}finally {
			lock.unlock();
		}
		
		
//		AtomicInteger atomicInteger = tradeVersion.get(trade.getVersion());
//		if (atomicInteger == null) { // If not in concurrentMap  then check in DB
//			Integer max = iStoreInterface.getMaxVersion(trade.getTradeId());
//			if (max != null) {
//				tradeVersion.putIfAbsent(trade.getTradeId(), new AtomicInteger(max));
//			}
//		}
//		
//		AtomicInteger atomicVersion = tradeVersion.putIfAbsent(trade.getTradeId(), new AtomicInteger(trade.getVersion()));
//		if (trade.getVersion() < atomicVersion.longValue()) {
//			throw new RuntimeException("Higher version already processed");
//		}
//		
//		if (atomicVersion.compareAndSet(atomicVersion.intValue(), trade.getVersion())) {
//			iStoreInterface.insertOrUpdateTrade(trade);
//		}else {
//			// 2 versions for the same trade are being processed at the same time
//		}
//		
	}

}
