package org.duckhawk.core;

public class TestMetadata {

    String testId;

    String productId;

    String productVersion;

    public TestMetadata(String testId, String productId, String productVersion) {
        super();
        this.testId = testId;
        this.productId = productId;
        this.productVersion = productVersion;
    }

    /**
     * Returns the test identifier
     * 
     * @return
     */
    public String getTestId() {
        return testId;
    }

    /**
     * The id of the product under test
     * 
     * @return
     */
    public String getProductId() {
        return productId;
    }

    /**
     * The version of the product under test
     * 
     * @return
     */
    public String getProductVersion() {
        return productVersion;
    }

}
