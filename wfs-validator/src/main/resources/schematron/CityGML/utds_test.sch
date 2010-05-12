<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:sch="http://purl.oclc.org/dsdl/schematron">
 
    <title>Test Schematron constraints for the OWS-6 CityGML-UTDS application schema</title>
 
    <ns prefix="sch" uri="http://purl.oclc.org/dsdl/schematron"/>
    <ns prefix="b" uri="http://www.opengis.net/citygml/building/1.0"/>
    <ns prefix="c" uri="http://www.opengis.net/citygml/1.0"/>
    <ns prefix="icism" uri="urn:us:gov:ic:ism:v2"/>
    <ns prefix="u" uri="http://www.opengis.net/ows-6/utds/0.3"/>
    <ns prefix="gml" uri="http://www.opengis.net/gml" />
 
    <pattern>
        <rule context="//u:Building">
            <assert test="count(b:class)=0 or b:class='1020' or b:class='1140' or b:class='1170' or b:class='1100' or b:class='1120' or b:class='1160' or b:class='1080' or b:class='1150' or b:class='1000'">The building with id '<value-of select="@gml:id"/>' is of a CityGML building class that is not allowed in UTDS.
            </assert>
            <assert test="(count(b:class)=0 or b:class!='1000' or (b:class='1000' and b:function='1000' and count(b:usage)=0))">The building with id '<value-of select="@gml:id"/>' of class 1000 has invalid function or usage values.
            </assert>
        </rule>

    </pattern>
    
    <pattern>
        <rule context="//u:Building">
            <let name="code" value="u:conditionOfFacility"/>
            <let name="codeSpaceDoc" value="document(u:conditionOfFacility/@codeSpace)/gml:Dictionary"/>
            <assert test="$codeSpaceDoc">Unable to find the condition-of-facility code list dictionary.</assert>
            <assert test="count($codeSpaceDoc/gml:dictionaryEntry/gml:Definition[gml:name=$code])=1">The building with id '<value-of select="@gml:id"/>' has a value for the condition of the facility (<value-of select="$code"/>) that is not part of the associated codelist.
            </assert>
        </rule>
    </pattern>
    
    <pattern>
    	<rule context="//u:Building">
			<assert test="b:lod1Solid/gml:Solid/@srsName='urn:ogc:def:crs:EPSG::4979' or ../../gml:boundedBy/gml:Envelope/@srsName='urn:ogc:def:crs:EPSG::4979'"> The building with id '<value-of select="@gml:id"/>' has a geometry that used an invalid coordinate reference system.
			</assert>
		</rule>
    </pattern>

    
    <pattern>
        <rule context="//u:Building">
            <assert test="b:measuredHeight/@uom='m'">The building with id '<value-of select="@gml:id"/>' has a measured height value that is not in metre, but in unit '<value-of select="b:measuredHeight/@uom"/>'.
            </assert>
        </rule>
    </pattern>
    
    <pattern>
        <rule context="//u:Building">
            <assert test="count(b:lod1Solid)=1">The building with id '<value-of select="@gml:id"/>' has no solid geometry on LoD 1.
            </assert>
        </rule>
    </pattern>
    
    <pattern>
        <rule context="//u:Building">
            <assert test="count(b:lod1Solid/gml:Solid)=1">The building with id '<value-of select="@gml:id"/>' has a solid geometry on LoD 1 which is not a gml:Solid.
            </assert>
        </rule>
    </pattern>
    
</schema>