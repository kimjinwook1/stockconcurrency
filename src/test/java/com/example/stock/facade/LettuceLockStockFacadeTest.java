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
class LettuceLockStockFacadeTest {

	@Autowired
	private LettuceLockStockFacade lettuceLockStockFacade;

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
	 *	- lettuce 장점
	 *	구현이 간단한다
	 *
	 *	- lettuce 단점
	 *	스핀락 방식이므로 redis에 부하를 줄 수 있다.
	 */

	@Test
	void 동시에_100개의_요청_Lettuce_Lock() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					lettuceLockStockFacade.decrease(1L, 1L);
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
