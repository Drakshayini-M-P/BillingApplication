package com.example.billingbackend.dto;

import com.example.billingbackend.model.Invoice;
import java.math.BigDecimal;
import java.util.List;

public class InvoiceOverviewDTO {
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal unpaidAmount;
    private long totalInvoiceCount;
    private long paidInvoiceCount;
    private long unpaidInvoiceCount;
    private List<Invoice> recentInvoices;

    // --- THIS IS THE MISSING CONSTRUCTOR ---
    public InvoiceOverviewDTO(BigDecimal totalAmount, BigDecimal paidAmount, BigDecimal unpaidAmount,
                              long totalInvoiceCount, long paidInvoiceCount, long unpaidInvoiceCount,
                              List<Invoice> recentInvoices) {
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.unpaidAmount = unpaidAmount;
        this.totalInvoiceCount = totalInvoiceCount;
        this.paidInvoiceCount = paidInvoiceCount;
        this.unpaidInvoiceCount = unpaidInvoiceCount;
        this.recentInvoices = recentInvoices;
    }

    // --- GETTERS AND SETTERS ---
    // These are also required for Spring to be able to convert the object to JSON.

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getUnpaidAmount() {
        return unpaidAmount;
    }

    public void setUnpaidAmount(BigDecimal unpaidAmount) {
        this.unpaidAmount = unpaidAmount;
    }

    public long getTotalInvoiceCount() {
        return totalInvoiceCount;
    }

    public void setTotalInvoiceCount(long totalInvoiceCount) {
        this.totalInvoiceCount = totalInvoiceCount;
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

    public List<Invoice> getRecentInvoices() {
        return recentInvoices;
    }

    public void setRecentInvoices(List<Invoice> recentInvoices) {
        this.recentInvoices = recentInvoices;
    }
}