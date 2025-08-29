package com.example.billingbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecentSellDTO {
    private String invoiceId;
    private LocalDateTime createdAt;
    private String customerName;
    private BigDecimal amount;
    private String paymentStatus;

    public RecentSellDTO(String invoiceId, LocalDateTime createdAt, String customerName, BigDecimal amount, String paymentStatus) {
        this.invoiceId = invoiceId;
        this.createdAt = createdAt;
        this.customerName = customerName;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

    
}