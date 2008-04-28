package org.duckhawk.report.model;

import org.duckhawk.core.TestType;

/**
 * @author   Andrea Aime (TOPP)
 */
public class Test {
    /**
     * @uml.property  name="id"
     */
    long id;

    /**
     * @uml.property  name="name"
     */
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
     * @uml.property  name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @uml.property  name="name"
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     * @uml.property  name="type"
     */
    public TestType getType() {
        return type;
    }

    /**
     * @param type
     * @uml.property  name="type"
     */
    public void setType(TestType type) {
        this.type = type;
    }

    /**
     * @return
     * @uml.property  name="product"
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @param product
     * @uml.property  name="product"
     */
    public void setProduct(Product product) {
        this.product = product;
    }
}
