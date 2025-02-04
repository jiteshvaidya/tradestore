package com.tradestore.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class TradeStoreDateUtil {
	public static Date getTodaysDateWithoutTimePart() {
		Date today = new Date();
		Date truncatedDate = DateUtils.truncate(today, Calendar.DAY_OF_MONTH);
		return truncatedDate;
	}


}
