package com.microservice.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeProduct {
	
	private Flux<String> typeProduct = Flux.just("CUENTA", "CREDITO");
	
	private Flux<String> typeAccount = Flux.just("AHORRO", "CORRIENTE", "PLAZO FIJO");
	
	private Flux<String> typeCredit = Flux.just("PERSONAL", "EMPRESARIAL", "TARJETA DE CREDITO", "ADELANTO EN EFECTIVO");

	public Flux<String> getTypeProduct() {
		return typeProduct;
	}

	public void setTypeProduct(Flux<String> typeProduct) {
		this.typeProduct = typeProduct;
	}

	public Flux<String> getTypeAccount() {
		return typeAccount;
	}

	public void setTypeAccount(Flux<String> typeAccount) {
		this.typeAccount = typeAccount;
	}

	public Flux<String> getTypeCredit() {
		return typeCredit;
	}

	public void setTypeCredit(Flux<String> typeCredit) {
		this.typeCredit = typeCredit;
	}
	
}
