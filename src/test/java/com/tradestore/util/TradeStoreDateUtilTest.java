package com.tradestore.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TradeStoreDateUtilTest {

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
	void testGetTodaysDateWithoutTimePart() {
		Date todaysDateWithoutTimePart = TradeStoreDateUtil.getTodaysDateWithoutTimePart();
		Calendar cal = Calendar.getInstance();
		cal.setTime(todaysDateWithoutTimePart);
		
		assertEquals(0,  cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,  cal.get(Calendar.MINUTE));
		assertEquals(0,  cal.get(Calendar.SECOND));
	}

}
