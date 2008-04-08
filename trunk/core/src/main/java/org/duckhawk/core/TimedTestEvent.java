package org.duckhawk.core;

import java.util.EventObject;

/**
 * This event is thrown to all {@link TimedTestListener} objects when a
 * performance test ends running
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public class TimedTestEvent extends EventObject {
    private static final long serialVersionUID = 3535774637140319731L;

    Throwable exception;

    double time;

    /**
     * Builds an event for the success case
     * 
     * @param source
     * @param time
     */
    public TimedTestEvent(TestExecutor source, double time) {
        super(source);
        this.time = time;
    }

    /**
     * Builds an event for the failure case, the execution ended with an
     * unexpected exception
     * 
     * @param source
     * @param exception
     */
    public TimedTestEvent(TestExecutor source, double time, Throwable exception) {
        super(source);
        this.time = time;
        this.source = source;
        this.exception = exception;
    }

    /**
     * Returns the {@link TestExecutor} that generated this event
     * 
     * @return
     */
    @Override
    public TestExecutor getSource() {
        return (TestExecutor) super.getSource();
    }

    /**
     * Returns the eventual exception occurred during the execution
     * 
     * @return the exception occurred, or null if the execution went fine
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * The time it took to run the {@link TestExecutor} (in case of an
     * exception, the time is partial, it may not be comparable to the time of a
     * successful outcome, and it's reported only for completeness)
     * 
     * @return the execution time, in seconds
     */
    public double getTime() {
        return time;
    }

}
