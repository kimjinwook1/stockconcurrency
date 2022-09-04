package com.example.stock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceTest {

	@Autowired
	private StockService stockService;

	@Autowired
	private StockRepository stockRepository;

	@BeforeEach
	void before() {
		Stock stock = Stock.builder()
				.productId(1L)
				.quantity(100L)
				.build();

		stockRepository.saveAndFlush(stock);
	}

	@AfterEach
	void after() {
		stockRepository.deleteAll();
	}

	@Test
	void stock_decrease() {
		//given
		stockService.decrease(1L, 1L);

		//when
		Stock findStock = stockRepository.findById(1L).orElseThrow();

		//then
		assertThat(findStock.getQuantity()).isEqualTo(99L);
	}
}
