/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 LISAsoft- http://www.lisasoft.com.
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

package com.lisasoft.wfsvalidator.validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.lisasoft.wfsvalidator.tests.WFSValidatorAbstractTest;
import com.lisasoft.wfsvalidator.util.FileUtils;

/**
 * Class for validating XML documents against a set of Schematron rules.
 * 
 * @author shansen
 * 
 */
public class SchematronValidator {

    private static final Logger log = Logger.getLogger(SchematronValidator.class);

    // Schematron response without any errors
    public static final String EMPTY_SCHEMATRON_RESULT = "";

    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    // List of files containing Schematron rules
    List<File> rules;

    // XSLT file that transforms XML encoded rules to a XSLT style sheet.
    File transformer;

    /**
     * This constructor loads all files with a certain extension into the rules-list.
     * 
     * It also loads the transformer.
     * 
     * @param folder
     *            containing Schematron files
     * @param extension
     *            extension identifying Schematron files
     * @param transformerFile
     *            Schematron to XSLT transformer
     */
    public SchematronValidator(String folder, String extension, String transformerFile) {

        this.rules = FileUtils.getFilesByExtension(folder, extension);
        this.transformer = new File(transformerFile);
    }

    /**
     * This constructor loads only a single file with Schematron rules.
     * 
     * It also loads the transformer.
     * 
     * @param filename
     *            file containing Schematron rules
     * @param transformerFile
     *            Schematron to XSLT transformer
     */
    public SchematronValidator(String filename, String transformerFile) {

        File r = new File(filename);
        this.rules = new ArrayList<File>();
        this.rules.add(r);
        this.transformer = new File(transformerFile);

    }

    /**
     * This constructor takes one Schematron file containing rules and the file name of the
     * Schematron-to-XSL transformer.
     * 
     * @param r
     *            rules file
     * @param transformerFile
     *            name of transformer file.
     */
    public SchematronValidator(File r, String transformerFile) {

        if (r == null) {
            log.error("File with Schematron rules is NULL!");
            throw new IllegalArgumentException("File shouldn't be null!");
        }

        this.rules = new ArrayList<File>();
        this.rules.add(r);
        this.transformer = new File(transformerFile);
    }

    /**
     * This constructor takes a list of Schematron files containing rules and the file name of the
     * Schematron-to-XSL transformer.
     * 
     * @param rs
     *            list of rules file
     * @param transformerFile
     *            name of transformer file.
     */
    public SchematronValidator(List<File> rs, String transformerFile) {

        if (rs == null) {
            log.error("List of Schematron rules is NULL!");
            throw new IllegalArgumentException("List shouldn't be null!");
        }

        this.rules = rs;
        this.transformer = new File(transformerFile);
    }

    /**
     * Validates the given String against the previously set Schematron rules.
     * 
     * @param xmlDoc
     *            String containing the XML document that will be validated
     * @throws ValidationException
     *             thrown if the document is not valid.
     * @throws UnsupportedEncodingException
     *             thrown if the text format is not supported
     * @throws TransformerException
     *             thrown if a "technical" error during the the XSL transformation occurred.
     */
    public void validate(String xmlDoc) {

        log.info("Starting Schematron Validation (String source)");

        StringBuffer sb = new StringBuffer();

        for (File rule : this.rules) {
            String buf = validateSchematron(xmlDoc, rule);
            log.debug(rule.getName() + ":\n" + buf);
            sb.append(buf);
        }

        String schematronResult = sb.toString();
        try {

            if (!EMPTY_SCHEMATRON_RESULT.equals(schematronResult)
                    && !XML_HEADER.equals(schematronResult)) {
                log.debug("Result is not empty!\n" + schematronResult);
                throw new ValidationError(WFSValidatorAbstractTest.formatXML(schematronResult));
            }

        } catch (TransformerFactoryConfigurationError e) {
            log.error("TransformerFactoryConfigurationError during SchematronValidation", e);
            if (!EMPTY_SCHEMATRON_RESULT.equals(schematronResult)
                    && !XML_HEADER.equals(schematronResult)) {
                log.debug("Result is not empty!\n" + schematronResult);
                throw new ValidationError(schematronResult);
            }
            /*
             * } catch (TransformerException e) { e.printStackTrace(); if
             * (!EMPTY_SCHEMATRON_RESULT.equals(schematronResult)) {
             * log.debug("Result is not empty! "+schematronResult); throw new
             * ValidationError(schematronResult); }
             */
        }
        log.info("Schematron Validation Complete");
    }

    /**
     * Validates the content of the given InputStream against the previously set Schematron rules.
     * 
     * @param xmlDoc
     */
    public void validate(InputStream xmlDoc) {

        log.info("Starting Schematron Validation (InputStream source)");

        StringBuffer sb = new StringBuffer();

        for (File rule : this.rules) {
            sb.append(validateSchematron(xmlDoc, rule));
        }

        String schematronResult = sb.toString();
        try {

            if (!EMPTY_SCHEMATRON_RESULT.equals(schematronResult)
                    && !XML_HEADER.equals(schematronResult)) {
                log.debug("Result is not empty! " + schematronResult);
                throw new ValidationError(WFSValidatorAbstractTest.formatXML(schematronResult));
            }

        } catch (TransformerFactoryConfigurationError e) {
            log.error("TransformerFactoryConfigurationError during SchematronValidation", e);
            if (!EMPTY_SCHEMATRON_RESULT.equals(schematronResult)
                    && !XML_HEADER.equals(schematronResult)) {
                log.debug("Result is not empty! " + schematronResult);
                throw new ValidationError(schematronResult);
            }
        }/*
          * catch (TransformerException e) { e.printStackTrace(); if
          * (!EMPTY_SCHEMATRON_RESULT.equals(schematronResult)) {
          * log.debug("Result is not empty! "+schematronResult); throw new
          * ValidationError(schematronResult); } }
          */

        log.info("Schematron Validation Complete");
    }

    /**
     * validates against a set of schematron rules from the give rules-file.
     * 
     * @param the
     *            response that will be validated
     * @throws ValidationException
     *             thrown if the document is not valid.
     * @throws UnsupportedEncodingException
     *             thrown if the text format is not supported
     * @throws TransformerException
     *             thrown if a "technical" error during the the XSL transformation occurred.
     * @throws IOException
     */
    public String validateSchematron(String xmlDoc, File rules) {

        log.info("Validating against: " + rules.getName());

        // Stream to buffer the transformed rules (XSLT)
        OutputStream pipe = new ByteArrayOutputStream();
        // results of the schematron validation
        OutputStream resultStream = new ByteArrayOutputStream();
        Result result = new StreamResult(resultStream);

        // create xslt
        try {
            transformXSLTFiles(rules, this.transformer, pipe);

            // apply the transformed rules
            javax.xml.transform.Source rulestream = new javax.xml.transform.stream.StreamSource(
                    new ByteArrayInputStream(((ByteArrayOutputStream) pipe).toByteArray()));
            transformXSLTStreams(new StreamSource(new StringReader(xmlDoc)), rulestream, result);

            String schematronResult = ((ByteArrayOutputStream) resultStream).toString("UTF-8");

            return schematronResult;

        } catch (TransformerException e) {
            log.error("Validating Schematron: TransformationException: " + e);
            throw new ValidationError(e);
        } catch (UnsupportedEncodingException e) {
            log.error("Validating Schematron: UnsupportedEncodingException: " + e);
            throw new ValidationError(e);
        }
    }

    /**
     * Validates the content of the given InputStream against the given set of Schematron rules.
     * 
     * @param xmlDoc
     */
    public String validateSchematron(InputStream xmlDoc, File rules) {

        log.info("Validating against: " + rules.getName());

        // Stream to buffer the transformed rules (XSLT)
        OutputStream pipe = new ByteArrayOutputStream();
        // results of the schematron validation
        OutputStream resultStream = new ByteArrayOutputStream();
        Result result = new StreamResult(resultStream);

        // create xslt
        try {
            transformXSLTFiles(rules, this.transformer, pipe);

            // apply the transformed rules
            javax.xml.transform.Source rulestream = new javax.xml.transform.stream.StreamSource(
                    new ByteArrayInputStream(((ByteArrayOutputStream) pipe).toByteArray()));
            transformXSLTStreams((Source) xmlDoc, rulestream, result);

            String schematronResult = ((ByteArrayOutputStream) resultStream).toString("UTF-8");

            return schematronResult;

        } catch (TransformerException e) {
            log.debug("Validating Schematron: TransformationException: " + e);
            throw new ValidationError(e);
        } catch (UnsupportedEncodingException e) {
            log.debug("Validating Schematron: UnsupportedEncodingException: " + e);
            throw new ValidationError(e);
        }

    }

    /**
     * Transforms schematron rules (or any other xml file) according to the given xslt file
     * 
     * @param rules
     *            the schematron rules as xml
     * @param xsltFile
     *            the xslt file transforming the rules
     * @param output
     *            receives the transformed rules
     * @throws TransformerException
     *             if something goes wrong during the transformation
     */
    private void transformXSLTFiles(File rules, File xsltFile, OutputStream output)
            throws TransformerException {
        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(rules);
        javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(
                xsltFile);
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(output);

        transformXSLTStreams(xmlSource, xsltSource, result);
    }

    /**
     * Transforms a streamsource containing xml according to given xslt (also in a stream source).
     * 
     * This function is used for schematron validation.
     * 
     * @param xml
     *            the xml that will be transformed
     * @param xslt
     *            the xslt file transforming the xml
     * @param result
     *            receives the result of the transformation
     * @throws TransformerException
     *             if something goes wrong during the transformation
     */
    private void transformXSLTStreams(Source xml, Source xslt, Result result)
            throws TransformerException {

        // create an instance of TransformerFactory
        javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory
                .newInstance();

        javax.xml.transform.Transformer trans = transFact.newTransformer(xslt);

        // transform xml
        trans.transform(xml, result);

    }

}
