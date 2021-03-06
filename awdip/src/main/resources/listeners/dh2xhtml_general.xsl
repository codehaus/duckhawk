<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet generates the general overview report. Another template
will create the detailed reports. Thus the detailed reports might not be
existent! -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="xml" encoding="UTF-8" indent="yes" 
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" 
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

<!-- Root of the test results XML out -->
<xsl:template match="/TestSummary">
  <html>
    <head>
      <title>DuckHawk Report</title>
      <meta http-equiv="content-type" content="text/html; charset=utf-8" />  
      <link rel="stylesheet" type="text/css" href="dh-report.css" />
    </head>
    <body>
      <h1>Test results for <xsl:value-of
select="TestInformation/productVersion/name"/>
<xsl:text> </xsl:text>
<xsl:value-of select="TestInformation/productVersion/version"/>
      </h1>
      <xsl:if test="TestResult/testProperties/entry[@key='description']">
        <h2>Description</h2>
          <xsl:call-template name="FormatText">
            <xsl:with-param name="text" select="TestResult/testProperties/entry[@key='description']"/>
          </xsl:call-template>             
      </xsl:if>

      <xsl:apply-templates select="/TestSummary/TestInformation"/>
      


<!-- red/green -->
        <div class="error-summary">
          <!-- process all <TestResult> of one test class -->
          <xsl:call-template name="ErrorSummary">
            <xsl:with-param name="testClasses">
              <xsl:for-each select="/TestSummary/TestResult/test[not(tokenize(@name,'#')[1] = preceding::test/tokenize(@name,'#')[1])]">
                <xsl:if test="position() &gt; 1">
                  <xsl:text>,</xsl:text>
                </xsl:if>
                <xsl:value-of select="tokenize(@name,'#')[1]"/>
                
              </xsl:for-each>
            </xsl:with-param>
            <xsl:with-param name="allTestResults" select="/TestSummary/TestResult"/>
          </xsl:call-template>
        </div>


       
      <!-- the report may (and probably will) contain several test classes
           => summary for every class -->
      <xsl:for-each select="/TestSummary/TestResult/test[not(tokenize(@name,'#')[1] = preceding::test/tokenize(@name,'#')[1])]">
        <xsl:variable name="testClass" select="tokenize(@name,'#')[1]"/>
        <h2 id="{$testClass}">Results for <xsl:value-of select="$testClass"/></h2>
        
            
        <div class="test-class-results">
          <!-- process all <TestResult> of one test class -->
          <xsl:call-template name="TestClassResults">
            <xsl:with-param name="testResults" select="/TestSummary/TestResult[starts-with(test/@name,concat($testClass,'#'))]"/>
          </xsl:call-template>
        </div>
      </xsl:for-each>
      
      

      
    </body>
  </html>
</xsl:template>


<!-- Summary which test classes contains a failed test and which don't
testClasses - comma seperated list of all classes of the test suite -->
<xsl:template name="ErrorSummary">
  <xsl:param name="testClasses"/>
  <xsl:param name="allTestResults"/>
  
  <xsl:variable name="testClassesTok" select="tokenize($testClasses,',')"/>

  
  <h3>Error Summary</h3>
  
  <div class="failed">
    <p>Test classes that contain errors</p>
    <ul>
      <xsl:variable name="output">
                
        
        <xsl:for-each select="$testClassesTok">
          <xsl:variable name="testClass" select="."/>
 
          <!-- All <TestResult>s that belong to the current test class -->
          <xsl:variable name="testResults" select="$allTestResults[starts-with(test/@name,concat($testClass,'#'))]"/>

          <xsl:if test="$testResults/testProperties/entry[@key='conf.errorCount'] &gt; 0">
            <xsl:variable name="className" select="tokenize($testResults[1]/test/@name,'#')[1]"/>
            <li>
              <a href="#{$className}"><xsl:value-of select="$className"/></a>
            </li>
          </xsl:if>
        </xsl:for-each>
      </xsl:variable>
       
      <xsl:choose>
        <xsl:when test="count($output/*) &gt; 0">
          <xsl:copy-of select="$output"/>
        </xsl:when>
        <xsl:otherwise>
          <li>No errors occured while testing</li>
        </xsl:otherwise>
      </xsl:choose>
    </ul>
  </div>
  
  <div class="passed">
    <p>Test classes without errors</p>
    <ul>
      <xsl:variable name="output">
        <xsl:for-each select="$testClassesTok">
          <xsl:variable name="testClass" select="."/>
 
          <!-- All <TestResult>s that belong to the current test class -->
          <xsl:variable name="testResults" select="$allTestResults[starts-with(test/@name,concat($testClass,'#'))]"/>
          
          <xsl:if test="not($testResults/testProperties/entry[@key='conf.errorCount'] &gt; 0)">
            <xsl:variable name="className" select="tokenize($testResults[1]/test/@name,'#')[1]"/>
            <li>
              <a href="#{$className}"><xsl:value-of select="$className"/></a>
            </li>
          </xsl:if>
        </xsl:for-each>
      </xsl:variable>
      
      <xsl:choose>
        <xsl:when test="count($output/*) &gt; 0">
          <xsl:copy-of select="$output"/>
        </xsl:when>
        <xsl:otherwise>
          <li><em>All</em> classes contain errors</li>
        </xsl:otherwise>
      </xsl:choose>
    </ul>
  </div>  
</xsl:template>



<xsl:template name="TestClassResults">
  <xsl:param name="testResults"/>
  
  <!-- Properties that are the same for the whole test class. It's almost
       the same output as the AllGeneralResultProperties template -->
  <xsl:call-template name="TestClassProperties">
    <xsl:with-param name="properties" select="$testResults[1]/testProperties/entry"/>
  </xsl:call-template>

  <!-- summary of one test class
       add hash (#) to the classname to prevent matching different classes
       with a same prefix -->
  <xsl:call-template name="TestClassSummary">
    <xsl:with-param name="testResults" select="$testResults"/>
  </xsl:call-template>
</xsl:template>




<!-- List all properties of a single test class -->
<xsl:template name="TestClassProperties">
  <xsl:param name="properties"/>

  <h3>Properties</h3>    

  <xsl:call-template name="GeneralResultProperties">
    <xsl:with-param name="heading" select="'Test Configuration'"/>
    <xsl:with-param name="properties">
      <xsl:call-template name="ExtractProperties">
        <xsl:with-param name="includes" select="'test.,stress.'"/>
        <xsl:with-param name="excludes" select="'test.description'"/>
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


<!-- Information about every tests mostly created by the summarizers -->
<xsl:template name="TestClassSummary">
  <xsl:param name="testResults"/>
  
  <h3>Summary</h3>
     <table class="test-class-summary">
        <thead>
          <tr>
              <th>Failure</th>
              <th>Name of the test</th>
            <xsl:for-each select="$testResults[1]/testProperties/entry[starts-with(@key,'perf.')]">
                <th>
                  <xsl:value-of select="substring-after(@key,'perf.')"/>
                </th>
            </xsl:for-each>
            <xsl:for-each select="$testResults[1]/testProperties/entry[starts-with(@key,'params.')]">
                <th>
                  <xsl:value-of select="substring-after(@key,'params.')"/>
                </th>
            </xsl:for-each>                 
          </tr>
        </thead>
        <tbody>
           <xsl:for-each select="$testResults/testProperties">
              <xsl:variable name="properties" select="entry"/>
              <xsl:variable name="methodName" select="tokenize(../test/@name,'#')[last()]"/>
              
              <!-- main results are always named *.xml, the detailed report is
                   in a subdirectory that is named as the main results file, but
                   without extension -->
              <xsl:variable name="inputDir" select="substring(document-uri(/),1,string-length(document-uri(/))-4)"/>
              <xsl:variable name="fullMethodName" select="translate(../test/@name, '#', '.')"/>
              <xsl:variable name="testOutputName" select="tokenize($inputDir, '/')[last()]"/>
              <!-- output filename for detailed reports (to link to them)-->
              <xsl:variable name="outputFile" select="concat($testOutputName, '/', $fullMethodName, '.html')"/>
              

              <!-- post processed entry -->
              <xsl:variable name="ppEntries">
                <xsl:for-each select="entry">
                  <xsl:call-template name="PostProcessEntry">
                    <xsl:with-param name="entry" select="."/>
                  </xsl:call-template>
                </xsl:for-each>
              </xsl:variable>
              
              <tr>
              <xsl:if test="position() mod 5 = 0">
                <xsl:attribute name="class">seperator</xsl:attribute>
              </xsl:if>                
               <xsl:choose>
                  <xsl:when test="$ppEntries/entry[@key='conf.errorCount'] &gt; 0">
                    <td class="failed">true</td>
                    <td><a href="{$outputFile}" class="failed">
                      <xsl:value-of select="$methodName"/></a></td>
                  </xsl:when>
                  <xsl:otherwise>
                    <td class="passed">false</td>
                    <td><a href="{$outputFile}" class="passed">
                      <xsl:value-of select="$methodName"/></a></td>
                  </xsl:otherwise>                
                </xsl:choose>                

                <xsl:for-each select="$ppEntries/entry[starts-with(@key,'perf.')]">
                  <td>
                    <xsl:value-of select="."/>
                  </td>
                </xsl:for-each>

                <xsl:for-each select="$ppEntries/entry[starts-with(@key,'params.')]">
                  <td>
                    <xsl:value-of select="."/>
                  </td>
                </xsl:for-each>
                
              </tr>
          </xsl:for-each>              
        </tbody>
      </table>
</xsl:template>


<!-- General information of the test run regarding the test suit -->
<xsl:template match="TestInformation">
  <h2>General Testing Environment Information</h2>
  <table>
    <thead>
      <tr>
        <th colspan="3">Environment</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>Test run date</td>
        <td><xsl:value-of select="date"/></td>
      </tr>
      <tr>
        <td>Product Name</td>
        <td><xsl:value-of select="productVersion/name"/></td>
      </tr>
      <tr>
        <td>Product Version</td>        
        <td><xsl:value-of select="productVersion/version"/></td>
      </tr>
    </tbody>
  </table>
 
  
  <xsl:call-template name="GeneralResultProperties">
    <xsl:with-param name="heading" select="'Environment properties'"/>
    <xsl:with-param name="properties">
      <xsl:call-template name="ExtractProperties">
        <xsl:with-param name="excludes" select="'description'"/>
        <xsl:with-param name="properties" select="environment/*"/>
      </xsl:call-template>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>



<!-- ######################################################################
The following templates are the same as for the detailed report. These
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
