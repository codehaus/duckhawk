<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet generates the general overview report. Another template
will create the detailed reports. Thus the detailed reports might not be
existent! -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="xml" encoding="UTF-8" indent="yes" 
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" 
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

<!-- List all properties of a single test -->
<xsl:template name="AllGeneralResultProperties">
  <xsl:param name="properties"/>
    

  <xsl:call-template name="GeneralResultProperties">
    <xsl:with-param name="heading" select="'Test Parameters'"/>
    <xsl:with-param name="properties">
      <xsl:call-template name="ExtractProperties">
        <xsl:with-param name="includes" select="'test.,stress.'"/>
        <xsl:with-param name="excludes" select="'test.description'"/>
        <xsl:with-param name="properties" select="$properties"/>
      </xsl:call-template>
    </xsl:with-param>
  </xsl:call-template>
  
  <xsl:call-template name="GeneralResultProperties">
    <xsl:with-param name="heading" select="'Performance'"/>
    <xsl:with-param name="properties">
      <xsl:call-template name="ExtractProperties">
        <xsl:with-param name="includes" select="'perf.'"/>
        <xsl:with-param name="properties" select="$properties"/>
      </xsl:call-template>
    </xsl:with-param>        
  </xsl:call-template>
  
  <xsl:call-template name="GeneralResultProperties">
    <xsl:with-param name="heading" select="'Errors'"/>
    <xsl:with-param name="properties">
      <xsl:call-template name="ExtractProperties">
        <xsl:with-param name="includes" select="'conf.error'"/>
        <xsl:with-param name="properties" select="$properties"/>
      </xsl:call-template>
    </xsl:with-param>    
  </xsl:call-template>
  
</xsl:template>



<!-- Template to extract nodes based substring selection.  A new node set is
returend with all nodes that matched the substrings. Adds a new attribute
"name", it's the key without the prefix it was selected with.
If no "includes" substring is specified, all entries will be included.
If no "excludes" substring is specified, no entries will be included.
Algorithm:
 1. Select all nodes in properties that match "include"
 2. Select only nodes in the result of 1. that don't match "exclude"
include - properties starting with these substring should be included
exclude - properties starting with these substring shouldn't be included
properties - nodes that will be processed
-->
<xsl:template name="ExtractProperties">
  <xsl:param name="includes"/>
  <xsl:param name="excludes"/>
  <xsl:param name="properties"/>
  

  <xsl:variable name="includesTok" select="tokenize($includes,',')"/>
  <xsl:variable name="excludesTok" select="tokenize($excludes,',')"/>
  

  <xsl:variable name="includeNodes">
    <xsl:choose>
      <xsl:when test="count($includesTok) &gt; 0">
        <xsl:for-each select="$includesTok">
          <xsl:variable name="include" select="."/>

          <xsl:for-each select="$properties[starts-with(@key,$include)]">
            <xsl:call-template name="PostProcessEntry">
                <xsl:with-param name="entry" select="."/>
                <xsl:with-param name="substrings" select="$include"/>
            </xsl:call-template>
          </xsl:for-each>
        </xsl:for-each>
      </xsl:when>
      <!-- no substring specified, use all entries -->
      <xsl:otherwise>
        <xsl:for-each select="$properties">
          <xsl:call-template name="PostProcessEntry">
            <xsl:with-param name="entry" select="."/>
            <xsl:with-param name="substrings" select="''"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="count($excludesTok) &gt; 0">
      <xsl:for-each select="$excludesTok">
        <xsl:variable name="exclude" select="."/>

        <xsl:for-each
select="$includeNodes/entry[not(starts-with(@key,$exclude))]">
          <xsl:copy-of select="."/>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:for-each select="$includeNodes/entry">
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<!-- Postprocess properties:
 - Nicer names (without prefix)
 - percentage from [0,1] to 0-100%
entry - the property to post-process
substrings - name will be cut off by a substring that matches (note: only the
    first match is processed)
-->
<xsl:template name="PostProcessEntry">
  <xsl:param name="entry"/>
  <xsl:param name="substrings"/>
    
    
  <xsl:variable name="key" select= "$entry/@key"/>
  <xsl:variable name="type" select= "$entry/@type"/>
  <xsl:variable name="substringsTok" select="tokenize($substrings,',')"/>
  
  <xsl:variable name="name">
    <xsl:variable name="substring" select="$substringsTok[starts-with($key,.)]"/>
    <xsl:value-of select="substring-after($key,$substring[1])"/>
  </xsl:variable>

  <xsl:variable name="entryRounded">
    <xsl:choose>
      <xsl:when test="@type = 'double' or @type = 'float'">
        <!--<xsl:value-of select="format-number($entry, '0.###')"/>-->
        <xsl:value-of select="format-number($entry, '0.000')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$entry"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="value">
    <xsl:choose>  
      <xsl:when test="contains(lower-case($key), 'percent')">
        <xsl:value-of select="number($entryRounded)*100"/>%
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$entryRounded"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>


  <xsl:element name="entry">
    <xsl:attribute name="key"><xsl:value-of select="$key"/></xsl:attribute>
    <xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
    <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
    <xsl:value-of select="$value"/>
  </xsl:element>

</xsl:template>



<!-- template for formatting a group of properties of a single test
heading - title for the table
properties - data to build the table upon
-->
<xsl:template name="GeneralResultProperties">
  <xsl:param name="heading"/>
  <xsl:param name="properties"/>

  <xsl:variable name="numProperties" select="count($properties/entry)"/>



  <xsl:if test="$numProperties &gt; 0">
    <table>
        <xsl:if test="lower-case($heading)='errors' and $properties/entry[@key='conf.errorCount'] &gt; 0">
          <xsl:attribute name="class">error-occured</xsl:attribute>
        </xsl:if>
      <thead>
        <tr>
          <th colspan="2"><xsl:value-of select="$heading"/></th>
        </tr>
      </thead>
      <tbody>
        <xsl:for-each select="$properties/entry">
          <tr>
            <td><xsl:value-of select="@name"/></td>
            <td><xsl:value-of select="."/></td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:if>
</xsl:template>


<xsl:template name="FormatText">
  <xsl:param name="text"/>
    
  <xsl:variable name="textTok" select="tokenize($text,'\n')"/>
    
  <xsl:for-each select="$textTok">
    <p><xsl:value-of select="."/></p>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
