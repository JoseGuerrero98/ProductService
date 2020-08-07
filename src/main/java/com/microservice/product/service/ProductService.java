package com.microservice.product.service;

import com.microservice.product.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

	public Flux<Product> findAll();
	public Flux<Product> validationProducts();
	public Flux<Product> findByClient(String idclient);
	public Mono<Product> findById(String id);
	public Mono<Product> createProduct(Product product);
	public Mono<Product> updateProduct(Product product, String id);
	public Mono<Product> updateAccount(Product product, String id);
	public Mono<Product> updateCredit(Product product, String id);
	public Mono<Void> deleteProduct(String id);
	public Mono<Boolean> existTransaction(String id);
	
}
