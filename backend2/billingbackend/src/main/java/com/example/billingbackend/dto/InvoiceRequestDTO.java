package com.example.billingbackend.dto;

import java.util.List;

public class InvoiceRequestDTO {
    private Long customerId;
    private List<InvoiceItemDTO> items;
    private String paymentStatus;

    // Inner class for the items
    public static class InvoiceItemDTO {
        private Long productId;
        private int quantity;
		public Long getProductId() {
			return productId;
		}
		public void setProductId(Long productId) {
			this.productId = productId;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
        
        
        // Getters and Setters
    }

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public List<InvoiceItemDTO> getItems() {
		return items;
	}

	public void setItems(List<InvoiceItemDTO> items) {
		this.items = items;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

    // Getters and Setters
}