package com.lisasoft.awdip.tests.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpException;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.junit3.StressTest;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.ConformanceSummarizer;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;

public class TestTest extends StressTest {

	List<List<String>> tests;
	String host;
	String port;
	String basePath;
	int counter = 0;
	Communication comm;
	//Request request;

	/** properties that should make it into the output */
	static final String KEY_BBOX = "params.boundingBox";
	static final String KEY_BBOX_SIZE = "params.boundingBoxSize";

	/** force properties to be in the output, even if "null" */
	static final String[] forcePropertyOutput = new String[]{
		KEY_BBOX,
		KEY_BBOX_SIZE
	}; 

	public TestTest() {
		// Load test, 1 thread  doing 10 requests, with a ramp
		// up time of 10 second	
		super(getContext(), 10, 1, 10);

		tests = csvReader("src/main/resources/tests/load/test.csv");
		if(tests != null && tests.get(0) != null && tests.get(0).size() >= 3) {
			host = (String)tests.get(0).get(0);
			port = (String)tests.get(0).get(1);
			basePath = (String)tests.get(0).get(2);

			tests.remove(0);
		} else {
			fail("Empty/Invalid file");
		}
	}

	public void setUp() {
		
		
		comm = new Communication(host, new Integer(port));

		
	}

	private Request mkRequest() {
		System.out.println("c:"+counter);
		
		Request request;
		
		List<String> params = tests.get(counter);

		if(params.size() < 2) {
			System.out.println("mkRequest failed");
			fail("not enough parameters");
			return null;
		} else {
						
			request = new Request(RequestMethod.GET, basePath);
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("service", "WFS");
			data.put("request","GetFeature");
			data.put("typeName", params.get(0));
			data.put("maxFeatures", params.get(1));
			
			request.setData(data);
			putCallProperty(TestExecutor.KEY_REQUEST, host+":"+basePath);
		}
		if (counter < tests.size()-1) {
			counter++;
		} else {
			counter = 0;
		}
		
		return request;
	}
	
	public void testMethod()  {
		System.out.println("TEST1!!");
		Request request = mkRequest();
		System.out.println("TEST2!!"+request);
        String response = "";
		try {
			System.out.println("TEST2B!!");
			response = comm.sendRequest(request);
			System.out.println("TEST2C!!");
		} catch (HttpException e) {
			System.out.println("TESTX!!"+e);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("TESTXX!!"+e);
			e.printStackTrace();
		} catch (Throwable e) {
			System.out.println("TESTXXX!!"+e);
			e.printStackTrace();
		}
        System.out.println("TEST3!!"+response);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
        System.out.println(counter+": "+response);

	}
	
	 public void checkMethod() {
	   System.out.println((String)getCallProperty(TestExecutor.KEY_RESPONSE));
	 }

	
	public static TestContext getContext() {
		
		TestContext context;
		       
            TestProperties env = new TestPropertiesImpl();
            env.put("description", "bla"
                    + "foo "
                    + "bar");
            context = new TestContext("Load", "1.0", env,
                    new PerformanceSummarizer(), //
                    new ConformanceSummarizer(), //
                    new PrintStreamListener(false, true), // 
                    new XStreamDumper(new File("./target/dh-report")));
        
        return context;
    }

	private List<List<String>> csvReader(String file){
		List<List<String>> returnValue = new ArrayList<List<String>>();

		File csv = new File(file);
		BufferedReader csvReader;
		try {
			csvReader = new BufferedReader(new FileReader(csv));
			String line = csvReader.readLine();
			StringTokenizer tokenizer;
			List<String> tokens;

			while (line != null) {
				//System.out.println(line);
				
				tokenizer = new StringTokenizer(line, ";");
				tokens = new ArrayList<String>();
				
				while (tokenizer.hasMoreTokens()) {
					tokens.add(tokenizer.nextToken());
					//System.out.println(tokens.get(tokens.size()-1));
				}
				
				returnValue.add(tokens);
				line = csvReader.readLine();
				

			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
			returnValue = null;
		} catch (IOException e) {
			System.out.println(e);
			returnValue = null;
		}

		return returnValue;

	}
}
