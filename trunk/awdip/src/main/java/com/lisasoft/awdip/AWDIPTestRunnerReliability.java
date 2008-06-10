package com.lisasoft.awdip;

import com.lisasoft.awdip.tests.reliability.ReliabilityAggregator;

public class AWDIPTestRunnerReliability {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	    ReliabilityAggregator ra = new ReliabilityAggregator();
	    ra.start(20);
	}
}
