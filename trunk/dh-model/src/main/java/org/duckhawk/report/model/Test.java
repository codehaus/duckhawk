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
