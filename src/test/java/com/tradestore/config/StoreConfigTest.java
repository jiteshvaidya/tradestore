package com.tradestore.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import com.tradestore.Application;

@TestConfiguration
@Import(Application.class)
public class StoreConfigTest {
	
}
