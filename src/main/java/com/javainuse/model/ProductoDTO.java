package com.javainuse.model;

import java.util.Optional;


public class ProductoDTO {
	private String name;
	private Long price;
	private Integer quantity;
	
	public ProductoDTO() {}
	
	public Optional<String> getName() {
		return Optional.ofNullable(this.name);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Optional<Long> getPrice() {
		return Optional.ofNullable(this.price);
	}
	
	public void setPrice(Long price) {
		this.price = price;
	}
	
	public Optional<Integer> getQuantity() {
		return Optional.ofNullable(this.quantity);
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
