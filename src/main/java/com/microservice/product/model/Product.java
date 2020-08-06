package com.microservice.product.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document( collection = "Product")

public class Product {
	
	@Id
	private String id;
	
	private String typeProduct;
	
	private String type;
	
	private String idClient;
	
	private Double amount;
	
	private String idBank;
	
	private Double cantTransaction;
	
	private Double creditAmount;
	
	private Boolean status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypeProduct() {
		return typeProduct;
	}

	public void setTypeProduct(String typeProduct) {
		this.typeProduct = typeProduct;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIdClient() {
		return idClient;
	}

	public void setIdClient(String idCliente) {
		this.idClient = idCliente;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getIdBank() {
		return idBank;
	}

	public void setIdBank(String idBank) {
		this.idBank = idBank;
	}

	public Double getCantTransaction() {
		return cantTransaction;
	}

	public void setCantTransaction(Double cantTransaction) {
		this.cantTransaction = cantTransaction;
	}

	public Double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}
