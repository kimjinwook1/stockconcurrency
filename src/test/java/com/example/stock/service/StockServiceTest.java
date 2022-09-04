package com.example.stock.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.stock.domain.Stock;
import com.example.stock.facade.OptimisticLockStockFacade;
import com.example.stock.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

	@Autowired
	private PessimisticLockStockService pessimisticLockStockService;

	@Autowired
	OptimisticLockStockFacade optimisticLockStockFacade;

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

	@Test
	void 동시에_100개의_요청() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Stock findStock = stockRepository.findById(1L).orElseThrow();

		//100 - (1*100) = 0
		assertThat(findStock.getQuantity()).isEqualTo(0L);

	}

	@Test
	void 동시에_100개의_요청_Pessimistic_Lock() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					pessimisticLockStockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Stock findStock = stockRepository.findById(1L).orElseThrow();

		//100 - (1*100) = 0
		assertThat(findStock.getQuantity()).isEqualTo(0L);
	}

	@Test
	void 동시에_100개의_요청_Optimistic_Lock() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					optimisticLockStockFacade.decrease(1L, 1L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Stock findStock = stockRepository.findById(1L).orElseThrow();

		//100 - (1*100) = 0
		assertThat(findStock.getQuantity()).isEqualTo(0L);
	}
}
