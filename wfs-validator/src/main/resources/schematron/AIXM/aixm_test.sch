<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:sch="http://purl.oclc.org/dsdl/schematron">
    <title>Test Schematron constraints for the OWS-6 CityGML-UTDS application schema</title>
    <ns prefix="sch" uri="http://purl.oclc.org/dsdl/schematron"/>
    <ns prefix="icism" uri="urn:us:gov:ic:ism:v2"/>
    <ns prefix="u" uri="http://www.opengis.net/ows-6/utds/0.3"/>
    <ns prefix="gml" uri="http://www.opengis.net/gml" />
    <ns prefix="aixm" uri="http://www.aixm.aero/schema/5.1" />
    <pattern>
        <rule context="*">
	    <assert test="not(./aixm:minimumLimit[@uom = 'FL'] or ./aixm:minimumLimit[@uom = 'SM'])">
		If the unit of measurement has the value 'FL' or 'SM', then the attribute CODE_DIST_VER_UPPER must have the value 'STD' (standard pressure).
	    </assert>
        </rule>
    </pattern>
    <pattern>
        <rule context="*">
	    <assert test="not(./aixm:minimumLimit[@uom = 'FL']) or (string-length(./aixm:minimumLimit[@uom]) = 3)">
		If upperLimit.UOM = 'FL' (flight level in hundreds of feet) then it should have exactly 3 digits.
	    </assert>
        </rule>
    </pattern>
</schema>
