package org.duckhawk.util;

import java.io.IOException;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class ExceptionConverterTest extends TestCase {

    private ExceptionConverter converter;

    @Override
    protected void setUp() throws Exception {
        converter = new ExceptionConverter();
    }

    public void testSingle() {
        Exception e = new Exception("I'm the ugly exception");
        String result = converter.convert(e);
        assertEquals("[" + e.getMessage() + "]", result);
    }

    public void testChain() {
        Exception e = new Exception("I'm the ugly exception");
        Exception eCause = new Exception("I'm the ugly exception cause");
        e.initCause(eCause);
        String result = converter.convert(e);
        result = result.substring(1, result.length() - 1);
        String[] lines = result.split(",\\s*");
        assertEquals(2, lines.length);
        assertEquals(e.getMessage(), lines[0]);
        assertEquals(eCause.getMessage(), lines[1]);
    }

    public void testRecursive() {
        Exception e1 = new Exception("I'm the ugly exception");
        Exception e2 = new Exception("I'm the ugly brother");
        e1.initCause(e2);
        e2.initCause(e1);
        String result = converter.convert(e1, 100);
        String[] lines = result.split(",\\s*");
        assertEquals(100, lines.length);
        for (int i = 0; i < lines.length; i++) {
            lines[i] = i % 2 == 0 ? e1.getMessage() : e2.getMessage();
        }
    }

    public void testVerbose() {
        Exception e1 = new IllegalArgumentException("I'm the ugly exception");
        Exception e2 = new IOException("I'm the ugly brother");
        e1.initCause(e2);
        e1.fillInStackTrace();
        e2.fillInStackTrace();
        converter.setVerbose(true);
        String result = converter.convert(e1, 100);
        System.out.println(result);
        // check the two messages are there
        String twoMessages = "^.*I'm the ugly exception.*I'm the ugly brother.*$";
        assertTrue(Pattern.compile(twoMessages,
                Pattern.MULTILINE | Pattern.DOTALL).matcher(result).matches());
        // make sure the stack trace part is there too by checking the "at <full
        // class name>" elements for this class are there
        assertTrue(Pattern.compile(stackTraceElement(),
                Pattern.MULTILINE | Pattern.DOTALL).matcher(result).matches());
    }

    private String stackTraceElement() {
        return "^(.*at org\\.duckhawk\\.util\\.ExceptionConverterTest\\.testVerbose.*){2}$";
    }
}
