package com.citrusmall.citrusstock.util;

import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.model.enums.ScanMode;

public class EnumLocalizer {

    public static String localizeGoodsStatus(GoodsStatus status) {
        if (status == null) return null;
        return switch (status) {
            case GENERATED -> "Сгенерирован";
            case STICKED -> "Наклеен";
            case SCANNED -> "Отсканирован";
            case SHIPPED -> "Отгружен";
            default -> status.name();
        };
    }

    public static String localizeZone(String zoneName) {
        if (zoneName == null) return null;
        return switch (zoneName) {
            case "RECEIVING" -> "Приемка";
            case "SHIPMENT" -> "Отгрузка";
            case "STORAGE" -> "Хранение";
            default -> zoneName;
        };
    }

    public static String localizeScanMode(ScanMode mode) {
        if (mode == null) return null;
        return switch (mode) {
            case ON_WAREHOUSE -> "На складе";
            case SHIPMENT -> "Отгрузка";
            default -> mode.name();
        };
    }
}
