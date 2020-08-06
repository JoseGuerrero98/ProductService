package com.microservice.product.repo;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.microservice.product.model.Product;

import reactor.core.publisher.Mono;

@Repository
public interface ProductRepo extends ReactiveMongoRepository<Product, String> {
	
	@Query("{ 'idBank' : ?0, 'idClient' : ?1, 'type' : ?2 }")
	public Mono<Product> validateTypeAccount(String idbank, String idclient, String type);
	
	@Query("{ 'idBank' : ?0, 'idClient' : ?1, 'type' : ?2 }")
	public Mono<Product> validateTypeCreditPers(String idbank, String idclient, String type);
	
}
