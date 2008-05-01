package org.duckhawk.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestContext {
    TestProperties environment;

    List<TestListener> listeners;

    String productId;

    String productVersion;

    public TestContext(String productId, String productVersion,
            TestProperties environment, TestListener... listeners) {
        if (productId == null)
            throw new IllegalArgumentException("ProductId not specified");
        if (productVersion == null)
            throw new IllegalArgumentException("VersionId not specified");
        this.environment = environment == null ? new TestPropertiesImpl(): environment;
        this.listeners = Collections.unmodifiableList(Arrays.asList(listeners));
        this.productId = productId;
        this.productVersion = productVersion;
    }

    public TestProperties getEnvironment() {
        // TODO: make this un-modifiable as well?
        return environment;
    }

    public List<TestListener> getListeners() {
        return listeners;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductVersion() {
        return productVersion;
    }
}
