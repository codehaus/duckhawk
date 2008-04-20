package org.duckhawk.core.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

public class PerformanceSummarizer {
    private static NumberFormat format = new DecimalFormat("0.#######");

    double min;

    double max;

    double total;

    int callCount;

    double average;
    
    double[] times;

    double median;
    

    public void start(int expectedCallCount) {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        total = 0;
        callCount = 0;
        average = 0;
        if(expectedCallCount >= 0)
            times = new double[expectedCallCount];
        else
            times = new double[50];
    }

    public void accumulate(double time) {
        if(callCount >= times.length) {
            double[] temp = new double[times.length * 3 / 2];
            System.arraycopy(times, 0, temp, 0, times.length);
            times = temp;
        }
            
        times[callCount] = time;
        callCount++;
        total += time;
        if (time < min)
            min = time;
        else if (time > max)
            max = time;
    }
    
    public void done() {
        Arrays.sort(times, 0, callCount);
        if(callCount % 2 == 0) {
            median = (times[callCount / 2] + times[callCount / 2 + 1]) / 2; 
        } else {
            median = times[callCount / 2];
        }
        times = null;
    }

    public double getTotal() {
        return total;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getCallCount() {
        return callCount;
    }

    public double getAverage() {
        return total / callCount;
    }
    
    public double getMedian() {
        return median;
    }

    @Override
    public String toString() {
        return "Call count: " + callCount + ", Avg: "
                + format.format(getAverage()) + ", Med: "
                + format.format(median) + ", Min time: "
                + format.format(min) + ", Max time: " + format.format(max);
    }
}
