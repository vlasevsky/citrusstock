package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.strategy.QrOutputStrategy;
import com.citrusmall.citrusstock.strategy.QrOutputStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QrOutputService {

    @Autowired
    private QrOutputStrategyFactory strategyFactory;

    /**
     * Generates output for the given product batch in the specified format.
     *
     * @param batchId the identifier of the ProductBatch
     * @param format  the desired format ("pdf", "zip", or "png")
     * @return a byte array containing the output
     * @throws Exception if an error occurs during generation
     */
    public byte[] generateOutput(Long batchId, String format) throws Exception {
        QrOutputStrategy strategy = strategyFactory.getStrategy(format);
        return strategy.generateOutput(batchId);
    }
    
    /**
     * Generates output for the given box in the specified format.
     *
     * @param boxId the identifier of the Box
     * @param format  the desired format ("pdf" or "png")
     * @return a byte array containing the output
     * @throws Exception if an error occurs during generation
     */
    public byte[] generateOutputForBox(Long boxId, String format) throws Exception {
        QrOutputStrategy strategy = strategyFactory.getStrategy(format);
        return strategy.generateOutputForBox(boxId);
    }

    /**
     * Returns the MIME type for the specified format.
     *
     * @param format the desired format
     * @return the MIME type
     */
    public String getContentType(String format) {
        return strategyFactory.getStrategy(format).getContentType();
    }

    /**
     * Returns the Content-Disposition header for the batch output.
     *
     * @param batchId the identifier of the ProductBatch
     * @param format the desired format
     * @return the Content-Disposition header value
     */
    public String getContentDisposition(Long batchId, String format) {
        return strategyFactory.getStrategy(format).getContentDisposition(batchId);
    }
    
    /**
     * Returns the Content-Disposition header for the box output.
     *
     * @param boxId the identifier of the Box
     * @param format the desired format
     * @return the Content-Disposition header value
     */
    public String getContentDispositionForBox(Long boxId, String format) {
        return strategyFactory.getStrategy(format).getContentDispositionForBox(boxId);
    }
}