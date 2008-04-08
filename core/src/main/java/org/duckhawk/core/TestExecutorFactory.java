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
    TestExecutor buildTestExecutor();
}
