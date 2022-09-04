package com.example.stock.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
	//id, productId, quantity
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long productId;
	private Long quantity;


	public void decrease(Long quantity) {
		if (this.quantity - quantity < 0) {
			throw new RuntimeException("foo");
		}

		this.quantity -= quantity;
	}
}
