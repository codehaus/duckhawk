package com.lisasoft.wfsvalidator.validator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class ResourceResolver implements LSResourceResolver {

    private static final Logger log = Logger.getLogger(ResourceResolver.class);
    private List<File> localSchemas = null;

    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        
        // log.info("Resolving " + namespaceURI + " : " + systemId);
        
        InputStream resourceAsStream = this.getClass().getResourceAsStream(systemId);
        if(resourceAsStream == null) {
            // try the classpath
            resourceAsStream = this.getClass().getResourceAsStream(systemId);
        }
        
        if(resourceAsStream == null && localSchemas != null) {
            // look through the list of local schemas
            String name = null;
            if(systemId.contains("/")) {
                name = systemId.substring(systemId.lastIndexOf("/") + 1);
            } else {
                name = systemId;
            }

            for(File s : localSchemas) {
                // log.info(s.getName() + " = " + name);
                if(s.getName().toLowerCase().equals(name.toLowerCase())) {
                    try {
                        resourceAsStream = new FileInputStream(s);
                        break;
                    } catch (FileNotFoundException e) {
                        log.warn("Local schema listed but doesn't exist: " + s);
                        resourceAsStream = null;
                    }
                }
            }
        }
        
        if(resourceAsStream == null) {
            // look for the file in relation to the baseURI
            String basePath = baseURI.substring(0, baseURI.lastIndexOf("/"));
            try {
                URL url = new URL(basePath + "/" + systemId);
                resourceAsStream = url.openStream();
            } catch(MalformedURLException e) {
                // it was only a stab in the dark anyway
                resourceAsStream = null;
            } catch (IOException e) {
                resourceAsStream = null;
            }
        }
        return new LSInputImpl(publicId, systemId, resourceAsStream);
    }    
    
    protected class LSInputImpl implements LSInput {

        private String publicId;

        private String systemId;

        public String getPublicId() {
            return publicId;
        }

        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        public String getBaseURI() {
            return null;
        }

        public InputStream getByteStream() {            
            return null;
        }

        public boolean getCertifiedText() {
            return false;
        }

        public Reader getCharacterStream() {
            return null;
        }

        public String getEncoding() {
            return null;
        }

        public String getStringData() {
            synchronized (inputStream) {
                try {
                    byte[] input = new byte[inputStream.available()];
                    inputStream.read(input);
                    String contents = new String(input);
                    return contents;
                } catch (IOException e) {
                    System.out.println(e.getClass().getName() + ": " + e.getMessage());
                    System.out.println("When resolving " + systemId);
                    return null;
                }
            }
        }

        public void setBaseURI(String baseURI) {
        }

        public void setByteStream(InputStream byteStream) {         
        }

        public void setCertifiedText(boolean certifiedText) {
        }

        public void setCharacterStream(Reader characterStream) {
        }

        public void setEncoding(String encoding) {
        }

        public void setStringData(String stringData) {           
        }

        public String getSystemId() {
            return systemId;
        }

        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

        public BufferedInputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(BufferedInputStream inputStream) {
            this.inputStream = inputStream;
        }

        private BufferedInputStream inputStream;

        public LSInputImpl(String publicId, String sysId, InputStream input) {
            this.publicId = publicId;
            this.systemId = sysId;
            this.inputStream = new BufferedInputStream(input);
        }

    }

    public void setSchemas(List<File> schemas) {
        this.localSchemas = schemas;
    }

}
