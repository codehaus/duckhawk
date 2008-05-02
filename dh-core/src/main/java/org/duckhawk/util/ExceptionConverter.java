package org.duckhawk.util;


/**
 * Simple utility class that does convert an exception into a string
 * representation of it by summarizing all the messages in the exception and its
 * causes
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public class ExceptionConverter {

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
            // loop
            // by adding a self loop failsafe and also a count to make sure
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
