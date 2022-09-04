package com.example.stock.facade;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.stock.domain.Stock;
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
class RedissonLockStockFacadeTest {


	@Autowired
	private RedissonLockStockFacade redissonLockStockFacade;

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

	/**
	 * Redisson
	 * pub-sub 구현의 기반 -> redis 부하를 줄여줌
	 *
	 * lettuce에 비해 구현이 조금 복잡함.
	 * 별도의 라이브러리를 사용해야함.
	 */
	@Test
	void 동시에_100개의_요청_Redisson_Lock() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					redissonLockStockFacade.decrease(1L, 1L);
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
