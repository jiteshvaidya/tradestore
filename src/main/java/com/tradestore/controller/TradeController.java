package com.tradestore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tradestore.domain.Trade;
/**
 * Utility controller to send message to kafka topic.
 * This can also server as a example of different mode of transmission
 */
@RestController
public class TradeController {
	private final Logger logger = LoggerFactory.getLogger(TradeController.class);
	@Autowired
	private KafkaTemplate<Object, Object> template;
	

	@PostMapping(path = "/send", consumes = "application/json")
	public void sendFoo(@RequestBody Trade trade) {
		
		assert trade.getTradeId() != null : "Trade ID cannot be null";
		assert trade.getMaturityDate() != null : "Maturity Date cannot be null";
		
		logger.info("sending trade id ={} , version ={}", trade.getTradeId(), trade.getVersion());
		this.template.send("trade", trade);
	}


}
