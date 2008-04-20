package org.duckhawk.core;

/**
 * Executes task to be timed once. <p>
 * The run method shall be stateless, that is, it must be possible to
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
    
}
