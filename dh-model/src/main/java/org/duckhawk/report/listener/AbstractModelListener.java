package org.duckhawk.report.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestSuiteListener;
import org.duckhawk.core.TestType;
import org.duckhawk.report.model.Product;
import org.duckhawk.report.model.ProductVersion;
import org.duckhawk.report.model.Test;
import org.duckhawk.report.model.TestCallDetail;
import org.duckhawk.report.model.TestResult;
import org.duckhawk.report.model.TestRun;

public abstract class AbstractModelListener implements TestListener,
        TestSuiteListener {

    /**
     * TODO: replace these with a GT2 SoftValueHashMap or anything else that
     * ensures these maps won't grow too much *
     */
    private Map<String, Map<String, TestRun>> testRunCache = new HashMap<String, Map<String, TestRun>>();

    private Map<TestMetadata, TestResult> testResultCache = new HashMap<TestMetadata, TestResult>();

    public void testSuiteStarting(TestContext context) {
        try {
            testSuiteStarting(getTestRun(context.getProductId(), context
                    .getProductVersion(), context.getEnvironment()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This event marks the end of the whole test suite run
     */
    public void testSuiteCompleted(TestContext context) {
        try {
            testSuiteCompleted(getTestRun(context.getProductId(), context
                    .getProductVersion(), context.getEnvironment()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            TestProperties callProperties, double time, Throwable exception) {
        TestResult result = getTestResult(metadata);
        String exceptionMessage = exception != null ? exception.getMessage()
                : null;
        TestCallDetail detail = new TestCallDetail(time, exception != null,
                exceptionMessage, result);
        detail.getCallProperties().clear();
        if (callProperties != null)
            detail.getCallProperties().putAll(callProperties);
        try {
            handleDetail(detail);
        } catch (Exception e) {
            throw new RuntimeException("Listener bombed out during execution",
                    e);
        }
    }

    public void testRunCompleted(TestMetadata metadata,
            TestProperties testProperties) {
        TestResult result = getTestResult(metadata);
        result.getTestProperties().clear();
        if (testProperties != null) {
            result.getTestProperties().clear();
            result.getTestProperties().putAll(testProperties);
        }
        // this result won't be needed anymore, remove from the cache to save
        // memory
        testResultCache.remove(metadata);

        try {
            testEnded(result);
        } catch (Exception e) {
            throw new RuntimeException("Listener bombed out during execution",
                    e);
        }
    }

    public void testRunStarting(TestMetadata metadata,
            TestProperties testProperties, int callNumber) {
        // forces the creation of the test run
        try {
            TestResult result = getTestResult(metadata);
            result.getTestProperties().clear();
            if (result != null) {
                result.getTestProperties().clear();
                if (testProperties != null)
                    result.getTestProperties().putAll(testProperties);
            }

            testStarting(result);
        } catch (Exception e) {
            throw new RuntimeException("Listener bombed out during execution",
                    e);
        }
    }

    protected TestRun getTestRun(String productId, String versionId, TestProperties environment) {
        TestRun run = null;
        Product product = null;
        ProductVersion productVersion = null;
        Map<String, TestRun> runs = testRunCache.get(productId);
        if (runs != null && runs.size() > 0) {
            run = runs.get(versionId);
            if (run == null) {
                productVersion = runs.values().iterator().next()
                        .getProductVersion();
                run = new TestRun(new Date(), false, productVersion);
                if(environment != null)
                    run.getEnvironment().putAll(environment);
                runs.put(versionId, run);
            }
        } else {
            product = new Product(productId);
            productVersion = new ProductVersion(product, versionId);
            run = new TestRun(new Date(), false, productVersion);
            runs = new HashMap<String, TestRun>();
            runs.put(versionId, run);
            if(environment != null)
                run.getEnvironment().putAll(environment);
            testRunCache.put(productId, runs);
        }
        return run;
    }

    protected TestResult getTestResult(TestMetadata metadata) {
        TestResult result = testResultCache.get(metadata);
        if (result == null) {
            TestRun testRun = getTestRun(metadata.getProductId(), metadata
                    .getProductVersion(), null);
            Test test = new Test(metadata.getTestId(), TestType.undetermined,
                    testRun.getProductVersion().getProduct());
            result = new TestResult(test, testRun);
            testResultCache.put(metadata, result);
        }
        return result;
    }

    protected abstract void testStarting(TestResult result) throws Exception;

    protected abstract void testEnded(TestResult result) throws Exception;

    protected abstract void handleDetail(TestCallDetail detail)
            throws Exception;

    protected abstract void testSuiteStarting(TestRun run) throws Exception;
    
    protected abstract void testSuiteCompleted(TestRun run) throws Exception;

}
