package com.tradestore.util;

import static org.assertj.core.api.Assertions.assertThatIOException;
import static org.assertj.core.api.Assertions.assertThatNoException;
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
	void testValidateIdNull() {
		IllegalArgumentException assertionError = assertThrows(IllegalArgumentException.class, () -> TradeValidationUtil.validateTrade(new Trade()));
		assertEquals("Trade ID can not be null", assertionError.getMessage());
	}
	@Test
	void testValidateMaturityDateNull() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		IllegalArgumentException assertionError = assertThrows(IllegalArgumentException.class, () -> TradeValidationUtil.validateTrade(trade));
		assertEquals("Maturity Date cannot be null", assertionError.getMessage());
	}
	@Test
	void testValidateIdWithSpecialChar() {
		Trade trade = new Trade();
		trade.setTradeId("fdd$@s");
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, +1);
		trade.setMaturityDate(instance.getTime());
		IllegalArgumentException assertionError = assertThrows(IllegalArgumentException.class, () -> TradeValidationUtil.validateTrade(trade));
		assertEquals("Trade ID can only be aphanumeric", assertionError.getMessage());
	}

	@Test
	void testValidateMaturityDateInThePastNull() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, -1);
		trade.setMaturityDate(instance.getTime());
		IllegalArgumentException assertionError = assertThrows(IllegalArgumentException.class, () -> TradeValidationUtil.validateTrade(trade));
		assertEquals("Maturity Date should be greater than today", assertionError.getMessage());
	}
	
	@Test
	void testValidateValid() {
		Trade trade = new Trade();
		trade.setTradeId("fdds");
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DAY_OF_MONTH, 1);
		trade.setMaturityDate(instance.getTime());
		TradeValidationUtil.validateTrade(trade);
		assertThatNoException();
	}


}
