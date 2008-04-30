/**
 * 
 */
package org.duckhawk.core;

public class StoppableTestExecutor implements TestExecutor {

    boolean canceled;
    boolean checkPerformed;
    boolean timedOut;

    public void cancel() throws Throwable {
        this.canceled = true;
    }

    public void check(TestProperties callProperties) throws Throwable {
        this.checkPerformed = true;
    }

    public void run(TestProperties callProperties) throws Throwable {
        long start = System.currentTimeMillis();
        while(true) {
            // sleep a very little while
            try {
                Thread.sleep(100);
            } catch(Exception e) {
                System.out.println("Stoppable test executor: " + e);
            }
            
            // check if cancelled 
            if(canceled)
                break;
            
            // make sure we don't stay here forever, the test should
            // not take more than 30 seconds
            if((System.currentTimeMillis() - start) > 30000)
                timedOut = true;
        }
    }
}