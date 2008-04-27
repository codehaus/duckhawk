package org.duckhawk.report.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestType;
import org.duckhawk.report.model.Product;
import org.duckhawk.report.model.ProductVersion;
import org.duckhawk.report.model.Test;
import org.duckhawk.report.model.TestCallDetail;
import org.duckhawk.report.model.TestResult;
import org.duckhawk.report.model.TestRun;

public abstract class AbstractModelListener implements TestListener {

    /**
     * TODO: replace these with a GT2 SoftValueHashMap or anything else that
     * ensures these maps won't grow too much *
     */

    private Map<String, Map<String, TestRun>> testRunCache = new HashMap<String, Map<String,TestRun>>();

    private Map<TestMetadata, TestResult> testResultCache = new HashMap<TestMetadata, TestResult>();

    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            TestProperties callProperties, double time, Throwable exception) {
        TestResult result = getTestResult(metadata);
        String exceptionMessage = exception != null ? exception.getMessage() : null; 
        TestCallDetail detail = new TestCallDetail(time, exception != null,
                exceptionMessage, result);
        Map<String, String> props = detail.getCallProperties();
        fillProperties(callProperties, props);
        try {
            handleDetail(detail);
        } catch(Exception e) {
            throw new RuntimeException("Listener bombed out during execution", e);
        }
    }

    private void fillProperties(TestProperties callProperties,
            Map<String, String> props) {
        for (String name : callProperties.keySet()) {
            Object value = callProperties.get(name);
            String converted = convert(value);
            props.put(name, converted);
        }
    }

    private String convert(Object value) {
        // TODO: use a flexible conversion strategy
        if(value != null)
            return value.toString();
        else
            return null;
    }

    public void testRunCompleted(TestMetadata metadata,
            TestProperties testProperties) {
        TestResult result = getTestResult(metadata);
        result.getTestProperties().clear();
        if(testProperties != null)
            fillProperties(testProperties, result.getTestProperties());
        try {
            testEnded(result);
        } catch(Exception e) {
            throw new RuntimeException("Listener bombed out during execution", e);
        }
    }

    public void testRunStarting(TestMetadata metadata,
            TestProperties testProperties, int callNumber) {
        // forces the creation of the test run
        try {
            TestResult testResult = getTestResult(metadata);
            testResult.getTestProperties().clear();
            if(testProperties != null)
                fillProperties(testProperties, testResult.getTestProperties());
            testStarting(testResult);
        } catch(Exception e) {
            throw new RuntimeException("Listener bombed out during execution", e);
        }
    }

    protected TestRun getTestRun(TestMetadata metadata) {
        TestRun run = null;
        Product product = null;
        ProductVersion productVersion = null;
        Map<String, TestRun> runs = testRunCache.get(metadata.getProductId());
        if (runs != null && runs.size() > 0) {
            run = runs.get(metadata.getProductVersion());
            if (run == null) {
                productVersion = runs.values().iterator().next()
                        .getProductVersion();
                run = new TestRun(new Date(), false, productVersion);
                runs.put(metadata.getProductVersion(), run);
            }
        } else {
            product = new Product(metadata.getProductId());
            productVersion = new ProductVersion(product, metadata
                    .getProductVersion());
            run = new TestRun(new Date(), false, productVersion);
            runs = new HashMap<String, TestRun>();
            runs.put(metadata.getProductVersion(), run);
            testRunCache.put(metadata.getProductId(), runs);
        }
        return run;
    }

    protected TestResult getTestResult(TestMetadata metadata) {
        TestResult result = testResultCache.get(metadata);
        if (result == null) {
            TestRun testRun = getTestRun(metadata);
            Test test = new Test(metadata.getTestId(), TestType.undetermined,
                    testRun.getProductVersion().getProduct());
            result = new TestResult(test, testRun);
            testResultCache.put(metadata, result);
        }
        return result;
    }
    
    protected abstract void testStarting(TestResult result) throws Exception;

    protected abstract void testEnded(TestResult result) throws Exception;

    protected abstract void handleDetail(TestCallDetail detail) throws Exception;

}
