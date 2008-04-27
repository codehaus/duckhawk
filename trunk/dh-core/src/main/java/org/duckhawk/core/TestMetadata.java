package org.duckhawk.core;

/**
 * Test metadata that holds test identification as well as all non time related properties that might be built at runtime by the test factory, the test itself, or the listeners.
 * @author  Andrea Aime (TOPP)
 */
public class TestMetadata {

    /**
     * @uml.property  name="testId"
     */
    String testId;

    /**
     * @uml.property  name="productId"
     */
    String productId;

    /**
     * @uml.property  name="productVersion"
     */
    String productVersion;

    /**
     * Builds a minimal
     * 
     * @param testId
     * @param productId
     * @param productVersion
     */
    public TestMetadata(String testId, String productId, String productVersion) {
        super();
        this.testId = testId;
        this.productId = productId;
        this.productVersion = productVersion;
    }

    /**
     * Returns the test identifier
     * @return
     * @uml.property  name="testId"
     */
    public String getTestId() {
        return testId;
    }

    /**
     * The id of the product under test
     * @return
     * @uml.property  name="productId"
     */
    public String getProductId() {
        return productId;
    }

    /**
     * The version of the product under test
     * @return
     * @uml.property  name="productVersion"
     */
    public String getProductVersion() {
        return productVersion;
    }

}