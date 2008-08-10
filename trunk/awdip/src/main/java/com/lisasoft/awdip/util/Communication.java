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

package com.lisasoft.awdip.util;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 * Helper class to make requests to a single server specified on class creation.
 * The class is thread safe, multiple threads can perform HTTP requests at the
 * same time
 */
public class Communication {
    /** Supported request methods */
    public enum RequestMethod {
        GET, POST
    };

    /** Hostname/-adress of the server to connect to */
    String host;

    /** Port to connect to */
    int port;

    /** Http client to make requests to server */
    HttpClient client;

    private MultiThreadedHttpConnectionManager manager;

    /**
     * Setting for the connection to a server
     * 
     * @param host
     *                Host of the server
     * @param port
     *                Port of the server
     */
    public Communication(String host, int port) {
        this.host = host;
        this.port = port;

        this.manager = new MultiThreadedHttpConnectionManager();
        this.client = new HttpClient(manager);
    }
    
    /**
     * Send request to server. HTTP GET will use all values of the key-value
     * pair, HTTP POST will only use the value of a key named "body".
     * 
     * @param request
     *                Request to the server
     * @return Response the server made
     * @throws IOException
     * @throws HttpException
     */
    public String sendRequest(Request request) throws HttpException,
            IOException {
        URI uri = new URI("http", null, host, port, request.getPath());
        System.out.println(uri);
        switch (request.getMethod()) {
        case GET:
            return sendGetRequest(request.getDataAsNameValuePairs(), uri);
        case POST:
            return sendPostRequest(request.getBody(), uri);
        default:
            throw new HttpException("Request Method not supported");
        }
    }

    /**
     * Send request with specific data to server (previously set data will be
     * replaced
     * 
     * @param request
     *                Request to the server
     * @return Response the server made
     * @throws IOException
     * @throws HttpException
     * @throws Exception
     */
    public String sendRequest(Request request, HashMap<String, String> data)
            throws HttpException, IOException {
        request.setData(data);
        return sendRequest(request);
    }

    /**
     * Send GET request to server
     * 
     * @param data
     *                Data to send
     * @param uri
     *                Adress to send the data
     * @return Response the server made
     * 
     * @throws HttpException
     * @throws IOException
     */
    private String sendGetRequest(NameValuePair[] data, URI uri)
            throws HttpException, IOException {
        GetMethod reqGet = new GetMethod();
        reqGet.setURI(uri);
        reqGet.setQueryString(data);
        client.executeMethod(reqGet);
        return reqGet.getResponseBodyAsString();
    }

    /**
     * Send POST request to server
     * 
     * Data is set as key/value pair.
     * 
     * @param data
     *                Data to send
     * @param uri
     *                Address to send the data
     * @return Response the server made
     * 
     * @throws HttpException
     * @throws IOException
     */
    private String sendPostRequest(String data, URI uri) throws HttpException,
            IOException {
        PostMethod reqPost = new PostMethod();
        reqPost.setURI(uri);
        reqPost.setRequestHeader("Content-type", "text/xml; charset=UTF-8");
        reqPost.setRequestEntity(new StringRequestEntity(data));

        client.executeMethod(reqPost);
        return reqPost.getResponseBodyAsString();
    }

    public static void main(String[] args) throws Exception {
        Communication com = new Communication("192.168.2.136", 8080);
        HashMap<String, String> data = new HashMap<String, String>();
        /*
         * // get request data.put("request", "GetFeature"); data.put("version",
         * "1.1.0"); data.put("typename", "topp:states");
         * data.put("outputFormat", "GML2"); data.put("FEATUREID", "states.3");
         * 
         * Request request = new Request(RequestMethod.GET, data,
         * "/geoserver/wfs"); String response = com.sendRequest(request);
         */

        // post request
        String body = "<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:topp=\"http://www.openplans.org/topp\" xmlns:wfs=\"http://www.opengis.net/wfs\"   xmlns:ogc=\"http://www.opengis.net/ogc\"   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"   xsi:schemaLocation=\"http://www.opengis.net/wfs                       http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\">   <wfs:Query typeName=\"topp:states\">      <ogc:Filter>        <ogc:FeatureId fid=\"states.3\"/>     </ogc:Filter>     </wfs:Query> </wfs:GetFeature>";
        data.put("url", "http://192.168.2.136:8080/geoserver/wfs");
        data.put("body", body);
        Request request = new Request(RequestMethod.GET,
                "/geoserver/TestWfsPost", data);
        String response = com.sendRequest(request);

        System.out.println(response);
    }

    public static String sendWFSPost(String host, int port,
            String geoserverLocation, String body) throws IOException {

        Communication comm = new Communication(host, port);
        URI uri = new URI("http", null, host, port, "/" + geoserverLocation);

        return comm.sendPostRequest(body, uri);

    }

    /**
     * Call this method to abruptly cease any communication with the server
     */
    public void dispose() {
        manager.shutdown();
    }
}
