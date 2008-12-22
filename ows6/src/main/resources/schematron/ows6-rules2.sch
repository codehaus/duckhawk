<schema xmlns="http://purl.oclc.org/dsdl/schematron">
 <title>A Schematron Mini-Schema for Schematron</title>
 <ns prefix="gml" uri="http://www.opengis.net/gml"/>
 <ns prefix="wfs" uri="http://www.opengis.net/wfs"/>
 <pattern>
   <rule context="wfs:FeatureCollection">
     <assert test="gml:ERROR">
         A wfs:FeatureCollection contains gml:featureMember.
     </assert>
   </rule>
 </pattern>

</schema>