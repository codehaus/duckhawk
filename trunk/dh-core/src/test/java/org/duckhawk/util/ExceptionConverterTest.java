package org.duckhawk.util;

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
            lines[i] = i % 2 == 0 ? e1.getMessage(): e2.getMessage();
        }
    }
}
