package org.duckhawk.core;

/**
 * Executes task to be timed once. <p>
 * Shall be stateless, that is, it must be possible to
 * call run <code>n</code> times, each time the same operation shall be executed.
 * @author Andrea Aime (TOPP)
 *
 */
public interface TestExecutor {
    /**
     * Executes the performance test.  
     * @throws Throwable
     */
    public void run() throws Throwable;
    
    /**
     * Returns the test identifier
     * @return
     */
    public String getTestId();
    
    /**
     * The id of the product under test
     * @return
     */
    public String getProductId();
    
    /**
     * The version of the product under test
     * @return
     */
    public String getProductVersion();
}
