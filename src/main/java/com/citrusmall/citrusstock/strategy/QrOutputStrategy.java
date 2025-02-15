package com.citrusmall.citrusstock.strategy;

public interface QrOutputStrategy {
    /**
     * Generates output for the given product batch ID in a specific format.
     *
     * @param batchId the identifier of the ProductBatch
     * @return a byte array containing the output (e.g., PDF, ZIP, or PNG)
     * @throws Exception if an error occurs during generation
     */
    byte[] generateOutput(Long batchId) throws Exception;

    /**
     * Returns the MIME type of the output.
     */
    String getContentType();

    /**
     * Returns the Content-Disposition header for the output.
     *
     * @param batchId the identifier of the ProductBatch
     */
    String getContentDisposition(Long batchId);
}
