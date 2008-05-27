package org.duckhawk.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Simple utility class that does convert an exception into a string
 * representation of it by summarizing all the messages in the exception and its
 * causes
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public class ExceptionConverter {

    protected boolean verbose;

    /**
     * A verbose exception converter will dump full stack traces, whilst a non
     * verbose one will just dump the chain of error messages
     * 
     * @return
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Sets/resets verbose mode
     * @param verbose
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Dumps an exception message along all its causes messages into a string
     * (uses 1000 as the default max message count)
     * 
     * @param t
     *                the exception to be dumped
     */
    public String convert(Throwable t) {
        return convert(t, 1000);
    }

    /**
     * Dumps an exception message along all its causes messages into a string
     * 
     * @param t
     *                the exception to be dumped
     * @param maxMessageCount
     *                max number of messages to be included in the summary (used
     *                to limit the size of the result, and also as a paranoid
     *                check against self looping exception causes)
     */
    public String convert(Throwable t, int maxMessageCount) {
        if (verbose) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bos);
            t.printStackTrace(ps);
            ps.flush();
            return bos.toString();
        } else {
            StringBuffer result = new StringBuffer();
            result.append("[");
            Throwable ex = t;
            int count = 0;
            do {
                // append the message if not empty
                count++;
                final String message = ex.getMessage();
                if (!"".equals(message))
                    result.append(message);

                // grab the cause and make sure we do not enter into an infinite
                // loop by adding a self loop failsafe and also a count to make
                // sure
                // 
                Throwable cause = ex.getCause();
                if (ex == cause || cause == null || count >= maxMessageCount) {
                    break;
                } else {
                    result.append(", ");
                    ex = cause;
                }
            } while (true);
            result.append("]");
            return result.toString();
        }
    }

}
