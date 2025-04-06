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
     * Generates output for the given box ID in a specific format.
     * Default implementation returns null, subclasses can override if needed.
     *
     * @param boxId the identifier of the Box
     * @return a byte array containing the output (e.g., PDF, PNG)
     * @throws Exception if an error occurs during generation
     */
    default byte[] generateOutputForBox(Long boxId) throws Exception {
        throw new UnsupportedOperationException("Box output not supported for this format");
    }

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
    
    /**
     * Returns the Content-Disposition header for the box output.
     * Default implementation returns a generic header, subclasses can override if needed.
     *
     * @param boxId the identifier of the Box
     */
    default String getContentDispositionForBox(Long boxId) {
        return "attachment; filename=qr_box_" + boxId + "." + getFileExtension();
    }
    
    /**
     * Returns the file extension for this strategy.
     * Default implementation extracts it from the content type.
     */
    default String getFileExtension() {
        String contentType = getContentType();
        if (contentType.contains("pdf")) return "pdf";
        if (contentType.contains("png")) return "png";
        if (contentType.contains("zip")) return "zip";
        return "bin";
    }
}
