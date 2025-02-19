package com.citrusmall.citrusstock.model.enums;

public enum ProductBatchStatus {
    REGISTERED,           // Batch registered but not yet processed
    WAITING_FOR_SCANNING, // QR codes printed, waiting for scan confirmation
    CONFIRMED,            // All boxes scanned and confirmed (new goods)
    SHIPPED               // Batch has been shipped out
}
