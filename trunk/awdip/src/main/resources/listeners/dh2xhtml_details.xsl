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

<xsl:include href="dh2xhtml_helper.xsl" />


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

</xsl:stylesheet>
