package com.microservice.product.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.product.model.Product;
import com.microservice.product.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService service;
	
	@GetMapping("/")
	public Flux<Product> findAll() {
		return service.findAll();
	}
	
	@GetMapping("/findbyid/{id}")
	public Mono<Product> findById(@PathVariable("id") String id) {
		return service.findById(id);
	}
	
	@GetMapping("/exist/{id}")
	public Mono<Boolean> existTransaction(@PathVariable("id") String id) {
		return service.existTransaction(id);
	}
	
	@PostMapping("/create")
	public Mono<ResponseEntity<Product>> createProduct(@RequestBody Product product) {
		return service.createProduct(product)
				.map(item -> 
					ResponseEntity.created(URI.create("/product".concat(item.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					.body(item)
						).switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));
	}
	
	@PutMapping("/update/{id}")
	public Mono<ResponseEntity<Product>> updateProduct(@RequestBody Product product, @PathVariable("id") String id) {
		return service.updateProduct(product, id)
				.map(item -> 
				ResponseEntity.created(URI.create("/product".concat(item.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(item)
				);
	}
	
	@PutMapping("/updateaccount/{id}")
	public Mono<ResponseEntity<Product>> updateAccount(@RequestBody Product product, @PathVariable("id") String id) {
		return service.updateAccount(product, id)
				.map(item -> 
				ResponseEntity.created(URI.create("/product".concat(item.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(item)
				);
	}
	
	@PutMapping("/updatecredit/{id}")
	public Mono<ResponseEntity<Product>> updateCredit(@RequestBody Product product, @PathVariable("id") String id) {
		return service.updateCredit(product, id)
				.map(item -> 
				ResponseEntity.created(URI.create("/product".concat(item.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(item)
				);
	}
	
	@DeleteMapping("/delete/{id}")
	public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable("id") String id) {
		return service.deleteProduct(id).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
}
