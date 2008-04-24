package org.duckhawk.report.model;

import org.duckhawk.core.TestType;

public class Test {
    long id;

    String name;

    TestType type;

    Product product;

    protected Test() {

    }

    public Test(String name, TestType type, Product product) {
        super();
        this.name = name;
        this.type = type;
        this.product = product;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestType getType() {
        return type;
    }

    public void setType(TestType type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
