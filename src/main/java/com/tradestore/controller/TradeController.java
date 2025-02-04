package com.tradestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tradestore.domain.Trade;
/**
 * Utility controller to send message to kafka topic.
 * This can also server as a example of different mode of transmission
 */
@RestController
public class TradeController {
	
	@Autowired
	private KafkaTemplate<Object, Object> template;
	

	@PostMapping(path = "/send")
	public void sendFoo(Trade trade) {
		assert trade.getTradeId() != null : "Trade ID cannot be null";
		assert trade.getMaturityDate() != null : "Maturity Date cannot be null";
		this.template.send("trade", trade);
	}


}
