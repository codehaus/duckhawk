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

package org.duckhawk.report.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;

/**
 * @author   Andrea Aime (TOPP)
 */
public class TestRun {
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
    /**
     * @uml.property  name="id"
     */
    long id;

    /**
     * @uml.property  name="date"
     */
    Date date;

    /**
     * If true this run will be used as a base for comparisons against the latest runs
     * @uml.property  name="reference"
     */
    boolean reference;

    ProductVersion productVersion;
    
    TestProperties environment;

    protected TestRun() {
    }

    public TestRun(Date date, boolean reference, ProductVersion productVersion) {
        super();
        this.date = date;
        this.reference = reference;
        this.productVersion = productVersion;
    }

    /**
     * @return
     * @uml.property  name="id"
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     * @uml.property  name="id"
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return
     * @uml.property  name="date"
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     * @uml.property  name="date"
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return
     * @uml.property  name="reference"
     */
    public boolean isReference() {
        return reference;
    }

    /**
     * @param reference
     * @uml.property  name="reference"
     */
    public void setReference(boolean reference) {
        this.reference = reference;
    }

    /**
     * @return
     * @uml.property  name="productVersion"
     */
    public ProductVersion getProductVersion() {
        return productVersion;
    }

    /**
     * @param productVersion
     * @uml.property  name="productVersion"
     */
    public void setProductVersion(ProductVersion productVersion) {
        this.productVersion = productVersion;
    }

    public String getIdentifier() {
        return productVersion.getProduct().getName() + "-" + productVersion.getVersion() + "-" + ISO_FORMAT.format(getDate());
    }
    
    /**
     * @return
     * @uml.property  name="testProperties"
     */
    public TestProperties getEnvironment() {
        if (environment == null)
            environment = new TestPropertiesImpl();
        return environment;
    }
}
