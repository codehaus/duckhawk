package org.duckhawk.report.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author   Andrea Aime (TOPP)
 */
public class TestRun {
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
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
}
