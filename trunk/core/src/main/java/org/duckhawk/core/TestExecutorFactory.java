package org.duckhawk.core;

/**
 * Implementors are required to build a TestExecutor on demand.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface TestExecutorFactory {
    /**
     * Builds a test executor
     * 
     * @return
     */
    TestExecutor createTestExecutor();
    
    /**
     * Creates the metadata object
     * @see TestMetadata
     * @return
     */
    TestMetadata createMetadata();
}
