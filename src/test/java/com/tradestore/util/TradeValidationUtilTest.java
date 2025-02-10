package com.tradestore.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tradestore.domain.Trade;

class TradeValidationUtilTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIsValidTradeIdNull() {
		IllegalArgumentException assertionError = assertThrows(IllegalArgumentException.class, () -> TradeValidationUtil.isValidTrade(new Trade()));
		assertEquals("Trade ID can not be null", assertionError.getMessage());
	}
	@Test
	void testIsValidTradeMaturityDateNull() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		IllegalArgumentException assertionError = assertThrows(IllegalArgumentException.class, () -> TradeValidationUtil.isValidTrade(trade));
		assertEquals("Maturity Date cannot be null", assertionError.getMessage());
	}
	@Test
	void testIsValidTradeIdWithSpecialChar() {
		Trade trade = new Trade();
		trade.setTradeId("fdd$@s");
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, +1);
		trade.setMaturityDate(instance.getTime());
		IllegalArgumentException assertionError = assertThrows(IllegalArgumentException.class, () -> TradeValidationUtil.isValidTrade(trade));
		assertEquals("Trade ID can only be aphanumeric", assertionError.getMessage());
	}

	@Test
	void testIsValidTradeMaturityDateInThePastNull() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, -1);
		trade.setMaturityDate(instance.getTime());
		IllegalArgumentException assertionError = assertThrows(IllegalArgumentException.class, () -> TradeValidationUtil.isValidTrade(trade));
		assertEquals("Maturity Date should be greater than today", assertionError.getMessage());
	}
	
	@Test
	void testIsValidTradeValid() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, 1);
		trade.setMaturityDate(instance.getTime());
		assertEquals(true, TradeValidationUtil.isValidTrade(trade));
	}


}
