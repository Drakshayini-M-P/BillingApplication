package com.example.billingbackend.dto;

import com.example.billingbackend.model.Invoice;
import java.math.BigDecimal; // Import BigDecimal
import java.util.List;

public class DashboardDataDTO {
    // Main counts
    private long customerCount;
    private long productCount;
    private long invoiceCount;
    
    // "Today" counts
    private long invoicesToday;
    private long customersToday;
    private long productsToday;
    
    // Counts for the chart
    private long paidInvoiceCount;
    private long unpaidInvoiceCount;
    
    // Table data
    private List<Invoice> recentSells;

    // A default constructor
    public DashboardDataDTO() {}

    // Constructor used by the controller
    public DashboardDataDTO(long customerCount, long productCount, long invoiceCount,
                              long invoicesToday, long customersToday, long productsToday,
                              long paidInvoiceCount, long unpaidInvoiceCount,
                              List<Invoice> recentSells) {
        this.customerCount = customerCount;
        this.productCount = productCount;
        this.invoiceCount = invoiceCount;
        this.invoicesToday = invoicesToday;
        this.customersToday = customersToday;
        this.productsToday = productsToday;
        this.paidInvoiceCount = paidInvoiceCount;
        this.unpaidInvoiceCount = unpaidInvoiceCount;
        this.recentSells = recentSells;
    }

	public long getCustomerCount() {
		return customerCount;
	}

	public void setCustomerCount(long customerCount) {
		this.customerCount = customerCount;
	}

	public long getProductCount() {
		return productCount;
	}

	public void setProductCount(long productCount) {
		this.productCount = productCount;
	}

	public long getInvoiceCount() {
		return invoiceCount;
	}

	public void setInvoiceCount(long invoiceCount) {
		this.invoiceCount = invoiceCount;
	}

	public long getInvoicesToday() {
		return invoicesToday;
	}

	public void setInvoicesToday(long invoicesToday) {
		this.invoicesToday = invoicesToday;
	}

	public long getCustomersToday() {
		return customersToday;
	}

	public void setCustomersToday(long customersToday) {
		this.customersToday = customersToday;
	}

	public long getProductsToday() {
		return productsToday;
	}

	public void setProductsToday(long productsToday) {
		this.productsToday = productsToday;
	}

	public long getPaidInvoiceCount() {
		return paidInvoiceCount;
	}

	public void setPaidInvoiceCount(long paidInvoiceCount) {
		this.paidInvoiceCount = paidInvoiceCount;
	}

	public long getUnpaidInvoiceCount() {
		return unpaidInvoiceCount;
	}

	public void setUnpaidInvoiceCount(long unpaidInvoiceCount) {
		this.unpaidInvoiceCount = unpaidInvoiceCount;
	}

	public List<Invoice> getRecentSells() {
		return recentSells;
	}

	public void setRecentSells(List<Invoice> recentSells) {
		this.recentSells = recentSells;
	}
    
    // Getters and Setters for all fields...
}