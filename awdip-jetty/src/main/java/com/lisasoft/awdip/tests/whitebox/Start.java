/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 TOPP - http://www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.lisasoft.awdip.tests.whitebox;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.BoundedThreadPool;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Jetty starter, will run geoserver inside the Jetty web container.<br>
 * Useful for debugging, especially in IDE were you have direct dependencies
 * between the sources of the various modules (such as Eclipse).
 *
 * @author wolf
 *
 */
public class Start {
    //private static final Logger log = org.geotools.util.logging.Logging.getLogger(Start.class.getName());

    public static void main(String[] args) {
        Server jettyServer = null;

        try {
            jettyServer = new Server();

            // don't even think of serving more than XX requests in parallel... we
            // have a limit in our processing and memory capacities
            BoundedThreadPool tp = new BoundedThreadPool();
            tp.setMaxThreads(50);

            SocketConnector conn = new SocketConnector();
            String portVariable = System.getProperty("jetty.port");
            int port = parsePort(portVariable);
            if(port <= 0)
            	port = 8080;
            conn.setPort(port);
            conn.setThreadPool(tp);
            conn.setAcceptQueueSize(100);
            jettyServer.setConnectors(new Connector[] { conn });

            WebAppContext wah = new WebAppContext();
            wah.setContextPath("/geoserver");
            wah.setWar("src/main/resources/war/geoserver.war");
            jettyServer.setHandler(wah);
            wah.setTempDirectory(new File("target/work"));

            jettyServer.start();

            // use this to test normal stop behaviour, that is, to check stuff that
            // need to be done on container shutdown (and yes, this will make 
            // jetty stop just after you started it...)
            // jettyServer.stop(); 
        } catch (Exception e) {
            //log.log(Level.SEVERE, "Could not start the Jetty server: " + e.getMessage(), e);
        	System.out.println("Could not start the Jetty server: " + e.getMessage());
            if (jettyServer != null) {
                try {
                    jettyServer.stop();
                } catch (Exception e1) {
                    //log.log(Level.SEVERE,"Unable to stop the " + "Jetty server:" + e1.getMessage(), e1);
                	System.out.println("Unable to stop the " + "Jetty server:" + e1.getMessage());
                }
            }
        }
    }

	private static int parsePort(String portVariable) {
		if(portVariable == null)
			return -1;
	    try {
	    	return Integer.valueOf(portVariable).intValue();
	    } catch(NumberFormatException e) {
	    	return -1;
	    }
	}
}
