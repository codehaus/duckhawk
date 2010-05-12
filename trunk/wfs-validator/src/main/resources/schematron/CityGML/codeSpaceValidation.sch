<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron"
    queryBinding="xslt2">
    <!--
        This Schematron schema looks up the codeSpace and checks that
        the codeSpaceValue in the XML metadata document are valid
        according to that codeSpace.
    -->
    <!-- 
        This script was developed for ANZLIC - the Spatial Information Council 
        by Geoscience Australia
        as part of a project to develop an XML implementation of the ANZLIC ISO Metadata Profile. 
        
        July 2007.
        
        This work is licensed under the Creative Commons Attribution 2.5 License. 
        To view a copy of this license, visit 
        http://creativecommons.org/licenses/by/2.5/au/ 
        or send a letter to 
        
        Creative Commons, 
        543 Howard Street, 5th Floor, 
        San Francisco, California, 94105, 
        USA.
    -->
    <sch:title>Check that the codeSpace exists and that the codeSpaceValue is in
        that codeSpace</sch:title>
    <sch:ns prefix="gml" uri="http://www.opengis.net/gml" />
    <sch:pattern id="checkcodeSpace">
        <sch:rule context="//*[@codeSpace]">
            <sch:let name="codeSpaceDoc" value="document(@codeSpace)/gml:Dictionary"/>
            <sch:assert test="$codeSpaceDoc">Unable to find the specified
                codeSpace document or codeSpaceDictionary node.</sch:assert>
            <sch:assert test="document(@codeSpace)//gml:Definition[gml:name = current()]/gml:name">Value <sch:value-of select="current()"/> is not in the specified codeSpace <!--sch:value-of select="@codeSpace"/-->.</sch:assert>
        </sch:rule>
    </sch:pattern>
</sch:schema>
