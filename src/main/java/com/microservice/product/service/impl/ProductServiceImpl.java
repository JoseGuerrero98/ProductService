package com.microservice.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservice.product.model.Product;
import com.microservice.product.model.TypeClient;
import com.microservice.product.model.TypeProduct;
import com.microservice.product.repo.ProductRepo;
import com.microservice.product.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepo repo;
	
	@Override
	public Flux<Product> findAll() {
		return repo.findAll();
	}
	
	@Override
	public Flux<Product> findByClient(String idclient) {
		return repo.findByClient(idclient);
	}
	
	@Override
	public Mono<Product> findById(String id) {
		return repo.findById(id);
	}
	
	@Override
	public Mono<Boolean> existTransaction(String id) {
		return repo.existsById(id);
	}
	
	@Override
	public Flux<Product> validationProducts() {
		
		return repo.findAll().flatMap(all -> {
			if(all.getAmount() < 0) {
				all.setStatus(false);
			}else {
				all.setStatus(true);
			}
			return repo.save(all);
			
		});
	}
	
	public Flux<Product> statusClient(String idclient) {
		
		return repo.validateStatus(idclient);
		
	}
	
	public Flux<Boolean> validateStatus(String idclient) {
		
		return repo.validateStatus(idclient).flatMap(status -> {
			return Flux.just(false);
		}).switchIfEmpty(Flux.just(true));
		
	}
	
	@Override
	public Mono<Product> createProduct(Product product) {
		
		System.out.println("**************************************************");
		System.out.println("**************************************************");
		System.out.println("**************************************************");
		
		Mono<Boolean> existbank = existBank(product.getIdBank());
		
		Mono<Boolean> existclient = existClient(product.getIdClient());
		
		Mono<Boolean> typeprod = responseValidateTypeProduct(product.getTypeProduct());
		
		Mono<Boolean> typeaccountcredit = validateTypeAccountOrCredit(product.getTypeProduct(), product.getType());
		
		Mono<Boolean> validateCreate = validateRespAccountOrCredit(product.getIdBank(), product.getIdClient(), product.getTypeProduct(), product.getType());
		
		return existbank.flatMap(bank -> {
			if(bank) {
				return existclient.flatMap(client -> {
					if(client) {
						
						Mono<Boolean> status = validateStatus(product.getIdClient()).next();
						
						return status.flatMap(sta -> {
							
							if(sta) {
								return typeprod.flatMap(productval -> {
									if(productval) {
										return typeaccountcredit.flatMap(accountcred -> {
											if(accountcred) {
												
												return validateCreate.flatMap(validate -> {
													
													if(validate) {
														return repo.save(product).flatMap(saveprod -> {
															System.out.println(saveprod.getId());
															System.out.println(saveprod.getTypeProduct());
															return repo.findById(saveprod.getId()).flatMap(updateprod -> {
																
																if(saveprod.getTypeProduct().equals("CUENTA")) {
																	updateprod.setCantTransaction(5.0);
																}
																
																if(saveprod.getTypeProduct().equals("CREDITO")) {
																	updateprod.setCreditAmount(200.0);
																	updateprod.setStatus(true);
																}
																
																return repo.save(updateprod);
															});
															//return Mono.empty();
														});
													}else {
														//mensaje de: 
														System.out.println("No se pudo crear la cuenta, si eres"
																+ " cliente personal no puede tener mas de una cuenta de ahorro, corriente o plazo fijo,"
																+ " si eres cliente empresarial solo puede tener cuentas corrientes");
														return Mono.empty();
													}
													
												});
												
											}else {
												//mensaje de: 
												System.out.println("El tipo de cuenta o credito que escogio no esta registrado");
												return Mono.empty();
											}
										});
										//return repo.save(product);
									}else {
										//mensaje de: 
										System.out.println("El tipo de producto no esta registrado");
										return Mono.empty();
									}
								});
							}else {
								System.out.println("Tiene una deuda en una de sus productos de credito, no puede acceder a un nuevo producto");
								return Mono.empty();
							}
							
						});
						
						/**/

					}else {
						//mensaje de: El cliente no existe
						System.out.println("El cliente no existe");
						return Mono.empty();
					}
				});
			}else {
				//mensaje de: El banco escogido no existe
				System.out.println("El banco escogido no existe");
				return Mono.empty();
			}
		});
		
	}
	
	@Override
	public Mono<Product> updateProduct(Product product, String id) {
		return repo.findById(id).flatMap(item -> {
			item.setAmount(product.getAmount());
			return repo.save(item);
		});
	}
	
	@Override
	public Mono<Product> updateAccount(Product product, String id) {
		return repo.findById(id).flatMap(item -> {
			item.setCantTransaction(product.getCantTransaction());
			item.setAmount(product.getAmount());
			return repo.save(item);
		});
	}
	
	public Mono<Product> updateCredit(Product product, String id) {
		return repo.findById(id).flatMap(item -> {
			item.setCreditAmount(product.getCreditAmount());
			item.setAmount(product.getAmount());
			return repo.save(item);
		});
	}
	
	@Override
	public Mono<Void> deleteProduct(String id) {
		try {
			return repo.findById(id).flatMap(item -> {
				return repo.delete(item);
			});
		} catch (Exception e) {
			return Mono.error(e);
		}
	}
	
	public Mono<Boolean> existClient(String id) {
		
		String url = "http://localhost:8082/client/exist/" + id;
		
		return WebClient.create()
				.get()
				.uri(url)
				.retrieve()
				.bodyToMono(Boolean.class);
		
	}
	
	public Mono<TypeClient> typeClient(String id) {
		String url = "http://localhost:8082/client/findbyid/" + id;
		
		return WebClient.create()
				.get()
				.uri(url)
				.retrieve()
				.bodyToMono(TypeClient.class);
	}
	
	public Mono<Boolean> existBank(String id) {
		
		String url = "http://localhost:8083/bank/exist/" + id;
		
		return WebClient.create()
				.get()
				.uri(url)
				.retrieve()
				.bodyToMono(Boolean.class);
		
	}
	
	public Flux<Boolean> validateTypeProduct(String typeproduct) {
		
		TypeProduct typeprod = new TypeProduct();
		
		Flux<String> products = typeprod.getTypeProduct();
		
		return products.flatMap(mapper -> {
			
			try {
				
				if(mapper.equals(typeproduct)) {
					
					return Flux.just(true);
					
				}
				
			} catch (Exception e) {
				return Flux.error(e);
			}
			
			return Flux.empty();
			
		});
		
	}
	
	public Mono<Boolean> responseValidateTypeProduct(String typeproduct) {
		
		Mono<Boolean> response = validateTypeProduct(typeproduct).next();
		
		return response.switchIfEmpty(Mono.just(false));
		
	}
	
	public Mono<Boolean> validateAccountPers(String idbank, String idclient, String type) {
		
		return repo.validateTypeAccount(idbank, idclient, type).flatMap(item -> {
			return Mono.just(false);
		}).switchIfEmpty(Mono.just(true));
		
	}
	
	public Mono<Boolean> validateCreditPers(String idbank, String idclient, String type) {
		//validando que un cliente personal no puede tener un credito empresarial
		if(type.equals("EMPRESARIAL")) {
			System.out.println("No puedo crear un credito empresarial siendo cliente personal");
			return Mono.just(false);
		}else {
			//validando que no tenga mas que un credito de diferente tipos de credito
			return repo.validateTypeCreditPers(idbank, idclient, type).flatMap(item -> {
				return Mono.just(false);
			}).switchIfEmpty(Mono.just(true));
		}
	}
	
	public Mono<Boolean> validateCreditEmp(String idbank, String idclient, String type) {
		//validando que un cliente empresarial no puede tener un credito personal
		if(type.equals("PERSONAL")) {
			System.out.println("No puedo crear un credito personal siendo cliente empresarial");
			return Mono.just(false);
		}else {
			//validando que no tenga mas que un credito de diferente tipos de credito
			return repo.validateTypeCreditPers(idbank, idclient, type).flatMap(item -> {
				return Mono.just(false);
			}).switchIfEmpty(Mono.just(true));
		}
	}
	
	public Mono<Boolean> validateAccountEmp(String type) {
		if(type.equals("CORRIENTE")) {
			return Mono.just(true);
		}
		return Mono.just(false);
	}
	
	public Mono<Boolean> validateTypeAccountOrCredit(String typeproduct, String type) {
		
		TypeProduct typeprod = new TypeProduct();
		Flux<String> account = typeprod.getTypeAccount();
		Flux<String> credit = typeprod.getTypeCredit();
		
		Mono<Boolean> response;
		
		switch (typeproduct) {
		case "CUENTA":
			response = responseValidateTypeAccountCredit(account, type);
			break;
		case "CREDITO":
			response = responseValidateTypeAccountCredit(credit, type);
			break;

		default:
			response = Mono.just(false);
			break;
		}
		
		return response;
		
	}
	
	public Mono<Boolean> validateRespAccountOrCredit(String idbank, String idclient,String typeproduct, String type) {
		
		Mono<TypeClient> typecli = typeClient(idclient);
		
		if(typeproduct.equals("CUENTA")) {
			return typecli.flatMap(client -> {
				if(client.getTypeclient().equals("PERSONAL")) {
					return validateAccountPers(idbank, idclient, type);
				}
				
				if(client.getTypeclient().equals("EMPRESARIAL")) {
					return validateAccountEmp(type);
				}
				return Mono.just(false);
			});
		}
		
		if(typeproduct.equals("CREDITO")) {
			return typecli.flatMap(client -> {
				if(client.getTypeclient().equals("PERSONAL")) {
					return validateCreditPers(idbank, idclient, type);
				}
				
				if(client.getTypeclient().equals("EMPRESARIAL")) {
					return validateCreditEmp(idbank, idclient, type);
				}
				return Mono.just(false);
			});
		}
		
		return Mono.just(false);
	}
	
	public Mono<Boolean> responseValidateTypeAccountCredit(Flux<String> typeAccountCredit, String type) {
		
		Mono<Boolean> response = validateTypeAccountCredit(typeAccountCredit, type).next();
		
		return response.switchIfEmpty(Mono.just(false));
		
	}
	
	public Flux<Boolean> validateTypeAccountCredit(Flux<String> typeAccountCredit, String type) {
		
		Flux<String> account = typeAccountCredit;
		
		return account.flatMap(mapper -> {
			
			try {
				
				if(mapper.equals(type)) {
					return Flux.just(true);
				}
				
			} catch (Exception e) {
				return Flux.error(e);
			}
			
			return Flux.empty();
			
		});
		
	}
	
}
