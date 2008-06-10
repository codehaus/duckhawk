<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet generates the general overview report. Another template
will create the detailed reports. Thus the detailed reports might not be
existent! -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="xml" encoding="UTF-8" indent="yes" 
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" 
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

<xsl:include href="dh2xhtml_helper.xsl" />


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

</xsl:stylesheet>
