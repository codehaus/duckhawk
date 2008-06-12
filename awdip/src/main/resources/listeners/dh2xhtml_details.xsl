<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet generated the detailed reports. These might be too big and
the xslt parser might run out of memory. This is even a good idea, as you don't
want to watch such a big HTML in the browsers. You would instead take a look at
the original XML output.
Another stylesheet generates the general overview report.
This file won't create any output on the given destination, but only the
detailed report files -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="xml" encoding="UTF-8" indent="yes" 
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" 
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

<!-- Root of the test results XML out -->
<xsl:template match="/TestSummary">
      <!-- the report may (and probably will) contain several test classes
           => summary for every class -->
      <xsl:for-each select="/TestSummary/TestResult/test[not(tokenize(@name,'#')[1] = preceding::test/tokenize(@name,'#')[1])]">
        <xsl:variable name="testClass" select="tokenize(@name,'#')[1]"/>

          <!-- process all <TestResult> of one test class -->
          <xsl:call-template name="TestClassSummary">
            <xsl:with-param name="testResults" select="/TestSummary/TestResult[starts-with(test/@name,concat($testClass,'#'))]"/>
          </xsl:call-template>

      </xsl:for-each>
</xsl:template>



<xsl:template name="TestClassSummary">
  <xsl:param name="testResults"/>
  
           <xsl:for-each select="$testResults/testProperties">
              <xsl:variable name="properties" select="entry"/>
              <xsl:variable name="methodName" select="tokenize(../test/@name,'#')[last()]"/>


              <!-- main results are always named *.xml, the detailed report is
                   in a subdirectory that is named as the main results file, but
                   without extension -->
              <xsl:variable name="inputDir" select="substring(document-uri(/),1,string-length(document-uri(/))-4)"/>
              <xsl:variable name="fullMethodName" select="translate(../test/@name, '#', '.')"/>
              <xsl:variable name="testOutputName" select="tokenize($inputDir, '/')[last()]"/>

              <!-- output filename for detailed reports -->
              <xsl:variable name="outputFile" select="concat($testOutputName, '/', $fullMethodName, '.html')"/>
              
              <!-- write details file -->
              <xsl:message><xsl:value-of select="$outputFile"/></xsl:message>
              <xsl:result-document href="{$outputFile}">
                <xsl:call-template name="ResultDetails">
                  <xsl:with-param name="docDetails" select="document(concat($inputDir, '/', $fullMethodName, '.xml'))"/>
                  <xsl:with-param name="fullMethodName" select="$fullMethodName"/>
                  <xsl:with-param name="generalProperties" select="$properties"/>
                </xsl:call-template>
              </xsl:result-document>                

          </xsl:for-each>              

</xsl:template>




<!-- Details of a single tests, information about every single call of the test
-->
<xsl:template name="ResultDetails">
  <xsl:param name="docDetails"/>
  <xsl:param name="fullMethodName"/>
  <xsl:param name="generalProperties"/>

    <html>
      <head>
        <title>DuckHawk Report - Details for <xsl:value-of select="$fullMethodName"/></title>
        <link rel="stylesheet" type="text/css" href="../dh-report.css" />
        <meta http-equiv="content-type" content="text/html; charset=utf-8" /> 
      </head>
      <body>
        
      <h1>Details for <xsl:value-of select="$fullMethodName"/></h1>
      <xsl:if test="$docDetails/TestCallDetails/TestCallDetail[1]/callProperties/entry[@key='test.description']">

        <h2>Description</h2>
          <xsl:call-template name="FormatText">
            <xsl:with-param name="text" select="$docDetails/TestCallDetails/TestCallDetail[1]/callProperties/entry[@key='test.description']"/>
          </xsl:call-template>     
      </xsl:if>
      
      <h2>General information</h2>

      <xsl:call-template name="AllGeneralResultProperties">
        <xsl:with-param name="properties" select="$generalProperties"/>
      </xsl:call-template>
      
      <xsl:call-template name="GeneralResultProperties">
        <xsl:with-param name="heading" select="'Test Parameters'"/>
          <xsl:with-param name="properties">
            <xsl:call-template name="ExtractProperties">
              <xsl:with-param name="includes" select="'params.'"/>
              <xsl:with-param name="properties" select="$generalProperties"/>
            </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>


      <h2>Details</h2>
      
      <xsl:variable name="entries">
        <xsl:for-each select="$docDetails/TestCallDetails/TestCallDetail/callProperties">
          <callProperties>
            <xsl:for-each select="entry[not(@key='test.description')]">
              <xsl:call-template name="PostProcessEntry">
                <xsl:with-param name="entry" select="."/>
                <xsl:with-param name="substrings" select="'test.'"/>
              </xsl:call-template>
            </xsl:for-each>
          </callProperties>           
        </xsl:for-each>
      </xsl:variable>


      <table>
        <thead>
          <tr>
            <th>Time</th>
            <th>failure</th>
            <th>failure message</th>
            
            <xsl:for-each select="$entries/callProperties[1]/entry">
              <th><xsl:value-of select="@name"/></th>
            </xsl:for-each>              
          </tr>
        </thead>
        <tbody>
          <xsl:for-each select="$docDetails/TestCallDetails/TestCallDetail">
            <xsl:variable name="detailPosition" select="position()"/>
            
            <tr>
              <xsl:if test="position() mod 5 = 0">
                <xsl:attribute name="class">seperator</xsl:attribute>
              </xsl:if>
              <td>
                <xsl:value-of select="time"/>
              </td>
              <xsl:choose>
                <xsl:when test="failed=boolean(1)">
                    <td class="failed">true</td>
                </xsl:when>
                <xsl:otherwise>
                    <td class="passed">false</td>
                </xsl:otherwise>
             </xsl:choose>
              
              <td>
                <xsl:choose>
                  <xsl:when test="failureMessage">
                    <xsl:value-of select="failureMessage"/>
                  </xsl:when>
                  <xsl:otherwise>
                    &#160;
                  </xsl:otherwise>
                </xsl:choose>
              </td>
              
              <xsl:for-each
select="$entries/callProperties[$detailPosition]/entry">
                <td>
                    <xsl:if test="string-length() &gt; 1024">
                      <xsl:attribute name="class">huge</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="."/>
                </td>
              </xsl:for-each>   
            </tr>
          </xsl:for-each>
        </tbody>
      </table>

    </body>
  </html>
</xsl:template>





<!-- ######################################################################
The following templates are the same as for the general report. These
should go into a seperate stylesheet, but you can't be included properly when
they are bundled within a jar file -->


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
