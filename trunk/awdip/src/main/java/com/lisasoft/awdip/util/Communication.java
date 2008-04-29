package com.lisasoft.awdip.util;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;



public class Communication {
    /** Supported request methods */
    public enum RequestMethod { GET, POST };
    
    /** Hostname/-adress of the server to connect to */
    String host;
    
    /** Port to connect to */
    int port;

    /** Http client to make requests to server */
    HttpClient client;
    
    /** Object for post requests */ 
    PostMethod reqPost;
    
    /** Object for get requests */
    GetMethod reqGet;


    /**
     * Setting for the connection to a server
     * 
     * @param host Host of the server
     * @param port Port of the server
     */
    public Communication(String host, int port) {
        this.host = host;
        this.port = port;
        
        this.client = new HttpClient();
        this.reqGet = new GetMethod();
        this.reqPost = new PostMethod();
    }    

    /**
     * Send request to server
     * 
     * @param request Request to the server
     * @return Response the server made
     * @throws IOException 
     * @throws HttpException 
     */
    public String sendRequest(Request request)
            throws HttpException, IOException {
        URI uri = new URI("http", null, host, port, request.getPath());

        switch (request.getMethod()) {
            case GET:
                return sendGetRequest(request.getDataAsNameValuePairs(), uri);
            case POST:
                return sendPostRequest(request.getDataAsNameValuePairs(), uri);
            default:
                throw new HttpException("Request Method not supported");
        }
    }
    /**
     * Send request with specific data to server (previously set data will be
     * replaced
     * 
     * @param request Request to the server
     * @return Response the server made
     * @throws IOException 
     * @throws HttpException 
     * @throws Exception
     */
    public String sendRequest(Request request, HashMap<String, String> data)
        throws HttpException, IOException  {
        request.setData(data);
        return sendRequest(request);
    }

    
    
    /**
     * Send GET request to server
     * 
     * @param data Data to send
     * @param uri Adress to send the data
     * @return Response the server made
     * 
     * @throws HttpException
     * @throws IOException
     */
    private String sendGetRequest(NameValuePair[] data, URI uri)
            throws HttpException, IOException {
        reqGet.setURI(uri);
        reqGet.setQueryString(data);
        client.executeMethod(reqGet);
        return reqGet.getResponseBodyAsString();
    }

    /**
     * Send POST request to server
     * 
     * @param data Data to send
     * @param uri Adress to send the data
     * @return Response the server made
     * 
     * @throws HttpException
     * @throws IOException
     */
    private String sendPostRequest(NameValuePair[] data, URI uri)
            throws HttpException, IOException {
        reqPost.setURI(uri);
        reqPost.setRequestBody(data);
        client.executeMethod(reqPost);
        return reqPost.getResponseBodyAsString();
    }
    
    
    
    public static void main(String[] args) throws Exception {
        Communication com = new Communication("192.168.2.136", 8080);
        HashMap<String,String> data = new HashMap<String, String>();
/*
        // get request
        data.put("request", "GetFeature");
        data.put("version", "1.1.0");
        data.put("typename", "topp:states");
        data.put("outputFormat", "GML2");
        data.put("FEATUREID", "states.3");
        
        Request request = new Request(RequestMethod.GET, data,
                "/geoserver/wfs");
        String response = com.sendRequest(request);
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
}

