package com.citrusmall.citrusstock.model.enums;

public enum Zone {
    RECEIVING, // Receiving zone (default when a box is created)
    STORAGE,   // Storage zone (after scanning new goods)
    SHIPMENT   // Shipment zone (when goods are being shipped)
}