package org.duckhawk.report.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestRun {
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    long id;

    Date date;

    /**
     * If true this run will be used as a base for comparisons against the
     * latest runs
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }

    public ProductVersion getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(ProductVersion productVersion) {
        this.productVersion = productVersion;
    }

    public String getIdentifier() {
        return productVersion.getProduct().getName() + "-" + productVersion.getVersion() + "-" + ISO_FORMAT.format(getDate());
    }
}
