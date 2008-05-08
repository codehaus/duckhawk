package com.lisasoft.awdip.util;

import java.util.HashMap;

import org.apache.commons.httpclient.NameValuePair;

import com.lisasoft.awdip.util.Communication.RequestMethod;


/**
 * A unified object to store the information about a GET or POST request
 * (without information about the server)
 * 
 * @author vmische
 */
public class Request {
    /** Method of the request (GET or POST) */
    RequestMethod method;
    
    /** Data of the request*/
    HashMap<String,String> data;
    
    /** Path to the requested file (with leading slash) */
    String path;

   
    /**
     * Unified request object for GET and POST requests
     * 
     * @param method Method that is used for the request (GET or POST)
     * @param data Data that will be send
     * @param path Path to the requested file (with leading slash)
     */
    public Request(RequestMethod method, String path,
            HashMap<String,String> data) {
        this.method = method;
        this.data = data;
        this.path = path;        
    }    
    /**
     * Unified request object for GET and POST requests
     * 
     * @param method Method that is used for the request (GET or POST)
     * @param path Path to the requested file (with leading slash)
     */
    public Request(RequestMethod method, String path) {
        this(method, path, null);
    }   
    
    
    /**
     * Get the request's data as NameValue array
     * @return Data of the request as NameValue array
     */
    public NameValuePair[] getDataAsNameValuePairs() {
        return Util.hashMapToNameValuePairArray(data);
    }

    
    /**
     * Returns the value of a key-value pair named "body"
     * 
     * @return value for key "body" (null if not set).
     */
    public String getBody() {
        return data.get("body");
    }
    
    /**
     * Get request method
     * 
     * @return Request method
     */
    public RequestMethod getMethod() {
        return method;
    }
    
    
    /**
     * Set request method
     * 
     * @param method Request method
     */
    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    /**
     * Get the request's data
     * @return Data of the request
     */
    public HashMap<String, String> getData() {
        return data;
    }

    /**
     * Set the request's data
     * @param data Data of the request
     */
    public void setData(HashMap<String, String> data) {
        this.data = data;
    }
    
    /**
     * Set path to requested file (with leading slash)
     * @return Path to requested file
     */
    public String getPath() {
        return path;
    }

    /**
     * Get path to requested file (with leading slash)
     * @param path Path to requested file
     */
    public void setPath(String path) {
        this.path = path;
    }
}
