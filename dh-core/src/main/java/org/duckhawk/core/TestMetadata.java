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
    
    TestType type;

    /**
     * Builds a minimal
     * 
     * @param testId
     * @param productId
     * @param productVersion
     */
    public TestMetadata(String productId, String productVersion, String testId, TestType type) {
        this.testId = testId;
        this.productId = productId;
        this.productVersion = productVersion;
        this.type = type;
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

    /**
     * The type of test being run
     * @return
     */
    public TestType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((productId == null) ? 0 : productId.hashCode());
        result = prime * result
                + ((productVersion == null) ? 0 : productVersion.hashCode());
        result = prime * result + ((testId == null) ? 0 : testId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TestMetadata other = (TestMetadata) obj;
        if (productId == null) {
            if (other.productId != null)
                return false;
        } else if (!productId.equals(other.productId))
            return false;
        if (productVersion == null) {
            if (other.productVersion != null)
                return false;
        } else if (!productVersion.equals(other.productVersion))
            return false;
        if (testId == null) {
            if (other.testId != null)
                return false;
        } else if (!testId.equals(other.testId))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return productId + "." + productVersion + "#" + testId + "(" + type + ")";
    }

}
