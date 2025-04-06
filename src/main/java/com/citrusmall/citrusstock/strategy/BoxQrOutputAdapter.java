package com.citrusmall.citrusstock.strategy;

import com.citrusmall.citrusstock.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Адаптер для работы с QR-кодами Box в паттерне Strategy.
 * Делегирует вызовы соответствующим стратегиям.
 */
@Component
public class BoxQrOutputAdapter {

    @Autowired
    private QrOutputStrategyFactory strategyFactory;

    /**
     * Генерирует выходные данные QR-кода для Box в указанном формате.
     *
     * @param boxId идентификатор коробки
     * @param format формат ("pdf", "png", "zip")
     * @return массив байтов с QR-кодом
     * @throws Exception в случае ошибки
     */
    public byte[] generateOutputForBox(Long boxId, String format) throws Exception {
        QrOutputStrategy strategy = strategyFactory.getStrategy(format);
        return strategy.generateOutputForBox(boxId);
    }

    /**
     * Возвращает тип содержимого для указанного формата.
     *
     * @param format формат
     * @return MIME-тип
     */
    public String getContentType(String format) {
        return strategyFactory.getStrategy(format).getContentType();
    }

    /**
     * Возвращает заголовок Content-Disposition для Box в указанном формате.
     *
     * @param boxId идентификатор коробки
     * @param format формат
     * @return заголовок Content-Disposition
     */
    public String getContentDispositionForBox(Long boxId, String format) {
        return strategyFactory.getStrategy(format).getContentDispositionForBox(boxId);
    }
} 