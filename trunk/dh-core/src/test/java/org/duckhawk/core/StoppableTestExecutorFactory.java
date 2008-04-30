package org.duckhawk.core;

import java.util.ArrayList;

public class StoppableTestExecutorFactory implements TestExecutorFactory {

    TestMetadata metadata;

    ArrayList<StoppableTestExecutor> executors;

    public StoppableTestExecutorFactory() {
        this.metadata = new TestMetadata("test", "whosGonnaTestTheTests", "0.1");
        this.executors = new ArrayList<StoppableTestExecutor>();
    }

    public TestMetadata createMetadata() {
        return metadata;
    }

    public TestExecutor createTestExecutor() {
        StoppableTestExecutor executor = new StoppableTestExecutor();
        executors.add(executor);
        return executor;
    }

}