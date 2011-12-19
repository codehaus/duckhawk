<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:sch="http://purl.oclc.org/dsdl/schematron">
	<title>Schematron constraints that check an AIXMBasicMessage is structurally conformant to a Digital NOTAM message.</title>
	<!--
		 The first pattern covers general DNES validation.
		 
		 Each pattern after this targets a DNES scenario, organised into alphabetical order of the scenario code. 
	 -->
	<ns prefix="sch" uri="http://purl.oclc.org/dsdl/schematron"/>
	<ns prefix="gml" uri="http://www.opengis.net/gml/3.2" />
	<ns prefix="aixm" uri="http://www.aixm.aero/schema/5.1" />
	<ns prefix="event" uri="http://www.aixm.aero/schema/5.1/event" />
	<ns prefix="message" uri="http://www.aixm.aero/schema/5.1/message" />
	<ns prefix="xlink" uri="http://www.w3.org/1999/xlink" />
	
	<pattern name="Digital NOTAM events - general validation">
		<!-- Rule: Event members of the message -->
		<rule context="//event:Event">
			<let name="scenario" value="event:timeSlice/event:EventTimeSlice/event:scenario"/>
			<let name="dnesVersion" value="'1.0'"/>
			<!-- Each Event must include a scenario attribute -->
			<assert id="ndes-3.4-1" test="$scenario">
				A digital NOTAM event must include a scenario attribute.
				<value-of select="name()" /> ID: <value-of select="@gml:id" />
			</assert>

			<!--
				In an AIXM file that contains an event encoding, the identifier shall 
				also include the version of the Event Specification according to which 
				the encoding was done.
			-->
			<assert id="ndes-2.5-1" test="contains($scenario, $dnesVersion)">
				Found an event with a version that doesn't match this validator.
				<value-of select="name()" /> ID: <value-of select="@gml:id" />
				Version expected: <value-of select="$dnesVersion" />
				Version found: <value-of select="$scenario" />
			</assert>
		
			<!-- the scenario attribute should be one of the following (perhaps later this should use the codespace mechanism? -->
			<assert id="ndes-3.4-2" test="contains('SAA.ACT.$dnesVersion,ATSA.ACT.$dnesVersion,SAA.NEW.$dnesVersion,ATSA.NEW.$dnesVersion,RTE.CLS.$dnesVersion,RTE.OPN.$dnesVersion,AD.CLS.$dnesVersion,RWY.CLS.$dnesVersion,NAV.UNS.$dnesVersion,OBS.NEW.$dnesVersion,OBS.WDN.$dnesVersion,TWY.CLS.$dnesVersion,AD.CONT.$dnesVersion,OTHER.$dnesVersion', @scenario)">
				<value-of select="$scenario"/> is not a valid scenario.
				<value-of select="name()" /> ID: <value-of select="@gml:id" />
			</assert>
		</rule>
	</pattern>


	<!-- Scenario: Aerodrome Closure - AD.CLS -->
	<pattern name="Digital NOTAM events - Scenarion: Aerodrome Closure">
		<!-- Rule: Event for this scenario -->
		<rule context="//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'AD.CLS')]">
			<!-- At least one AirportHeliport must be present and associated to an Event of scenario AD.CLS -->
			<assert id="dnes-er-4.8-1" test="concat('#',@gml:id) = (//aixm:AirportHeliport)/descendant::aixm:extension/*/event:theEvent/@xlink:href">
				An Aerodrome Closure scenario must include at least one AirportHeliport.
				Event ID: <value-of select="@gml:id" />
			</assert>
		</rule>
		
		<!-- Rule: TEMPDELTA time slice of any airportheliport associated to this event -->
		<rule context="//aixm:timeSlice[../../aixm:AirportHeliport][descendant::event:theEvent/@xlink:href = concat('#',//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'AD.CLS')]/@gml:id)][*/aixm:interpretation = 'TEMPDELTA']/child::*">
			<let name="featureId" value="../../@gml:id"/>
			<let name="elementName" value="local-name(../..)"/>
			<let name="opStatus" value="aixm:availability/aixm:AirportHeliportAvailability/aixm:operationalStatus"/>

			<!--
				As a minimum, in addition to the AIXM mandatory properties gml:validTime and 
				aixm:interpretation, the AirportHeliport TEMPDELTA TimeSlice shall contain at 
				least aixm:sequenceNumber and one aixm:availability element with at least the 
				aixm:operationalStatus descendant element specified (not NIL).
			 -->
			<assert id="dnes-4.8.5-1" test="aixm:sequenceNumber">
				The AirportHeliport TEMPDELTA TimeSlice shall contain a sequenceNumber.
				Name: <value-of select="name()" />
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.8.5-2" test="$opStatus">
				The AirportHeliport TEMPDELTA TimeSlice shall contain an operationalStatus.
				Name: <value-of select="name()" />
				ID: <value-of select="$featureId" />
			</assert>
			
			<!-- 
				For the AirportHeliportAvailability with operationalStatus=CLOSED 
				included in the TEMPDELTA, if aixm:AirportHeliportUsage.operations=ALL, 
				then aixm:priorPermission and/or aixm:type shall be specified (not NIL)
			-->
			<let name="testClosedAll" value="$opStatus='CLOSED' and aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:operation='ALL'"/>
			<assert id="dnes-4.8.5-3" test="not($testClosedAll) or ($testClosedAll and (aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:type or aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:priorPermission))">
				If the AirportHeliport TEMPDELTA is CLOSED for ALL operations, then either priorPermission or type must appear in the usage
				ID: <value-of select="$featureId" />
			</assert>

			<!-- 
				If aixm:AirportHeliportUsage.priorPermission is specified, then the 
				aixm:AirportHeliportUsage.type shall be "CONDITIONAL"
			 -->
			<assert id="dnes-4.8.5-4" test="not(aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:priorPermission) or (aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:priorPermission and aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage[aixm:type='CONDITIONAL'])">
				If aixm:AirportHeliportUsage.priorPermission is specified, then the aixm:AirportHeliportUsage.type shall be "CONDITIONAL"
				ID: <value-of select="$featureId" />
			</assert>

			<!-- 
				For the AirportHeliportAvailability with operationalStatus=CLOSED 
				included in the TEMPDELTA, any eventual child 
				aixm:AirportHeliportUsage.type cannot have any other type than 
				"PERMIT" or "CONDITIONAL".
			 -->
			<assert id="dnes-4.8.5-5" test="not($opStatus='CLOSED') or ($opStatus='CLOSED' and contains('CONDITIONAL,PERMIT',aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:type))">
				For the AirportHeliportAvailability with operationalStatus=CLOSED included in the TEMPDELTA, any eventual child aixm:AirportHeliportUsage.type cannot have any other type than "PERMIT" or "CONDITIONAL".
				ID: <value-of select="$featureId" />
			</assert>
			
			<!-- 
				If aixm:AirportHeliportUsage.type is specified (not NIL), then 
				at least one aixm:selection shall be specified (not NIL)
			-->
			<assert id="dnes-4.8.5-6" test="not(aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:type) or (aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:type and aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:selection)">
				If aixm:AirportHeliportUsage.type is specified (not NIL), then at least one aixm:selection shall be specified (not NIL)
				ID: <value-of select="$featureId" />
			</assert>
			
			<!-- 
				Only the following properties of AircraftCharacteristics can be used in this 
				scenario: type, engine, wingSpan and wingSpanInterpretation, weight and 
				weightInterpretation 
			-->
			<assert id="dnes-4.8.5-7" test="not(aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:selection/descendant::aixm:AircraftCharacteristic[not(aixm:type | aixm:engine | aixm:wingSpan | aixm:wingSpanInterpretation | aixm:weight | aixm:weightInterpretation)])">
				Only the following properties of AircraftCharacteristics can be used in this scenario: type, engine, wingSpan and wingSpanInterpretation, weight and weightInterpretation.
				Name: <value-of select="name(aixm:availability/aixm:AirportHeliportAvailability/aixm:usage/aixm:AirportHeliportUsage/aixm:selection/descendant::aixm:AircraftCharacteristic/*)" />
				ID: <value-of select="$featureId" />
			</assert>
		</rule>
	</pattern>



	<!-- Scenario: Navaid Unservicable - NAV.UNS -->
	<pattern name="Digital NOTAM events - Scenarion: Navaid Unservicable">
		<!-- Rule: Event for this scenario -->
		<rule context="//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'NAV.UNS')]">
			<!-- At least one Navaid, NavaidEquipment or appropriate subclass must be present and associated to an Event of scenario NAV.UNS -->
			<assert id="dnes-er-4.10-1" test="concat('#',@gml:id) = (//aixm:Navaid | //aixm:NavaidEquipment | //aixm:DME | //aixm:MarkerBeacon | //aixm:NDB | //aixm:SDF | //aixm:TACAN | //aixm:VOR)/descendant::aixm:extension/*/event:theEvent/@xlink:href">
				A Navaid Unservicable scenario must include at least one AIXM Navaid, NavaidEquipment or NavaidEquipment subclass such as TACAN or VOR.
				Event ID: <value-of select="@gml:id" />
			</assert>
		</rule>
		
		<!-- Rule: TEMPDELTA time slice of any navaid associated to this event -->
		<rule context="//aixm:timeSlice[../../aixm:Navaid | ../../aixm:NavaidEquipment | ../../aixm:DME | ../../aixm:MarkerBeacon | ../../aixm:NDB | ../../aixm:SDF | ../../aixm:TACAN | ../../aixm:VOR][descendant::event:theEvent/@xlink:href = concat('#',//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'NAV.UNS')]/@gml:id)][*/aixm:interpretation = 'TEMPDELTA']/child::*">
			<let name="featureId" value="../../@gml:id"/>
			<let name="elementName" value="local-name(../..)"/>
			<let name="opStatus" value="aixm:availability/aixm:NavaidOperationalStatus/aixm:operationalStatus"/>
			<let name="navaidElement" value="//aixm:Navaid[aixm:timeSlice/aixm:NavaidTimeSlice/aixm:navaidEquipment/aixm:NavaidComponent/aixm:theNavaidEquipment/@xlink:href=concat('#',$featureId)]"/>
			<let name="navaidOpStatus" value="$navaidElement/aixm:timeSlice/aixm:NavaidTimeSlice/aixm:availability/aixm:NavaidOperationalStatus/aixm:operationalStatus"/>

			<!--
				As a minimum, in addition to the AIXM mandatory properties gml:validTime and 
				aixm:interpretation, the Navaid and NavaidEquipment TEMPDELTA TimeSlice shall 
				contain at least aixm:operationalStatus
			 -->
			<assert id="dnes-4.10.5-1" test="$opStatus">
				The Navaid or navaid equipment TEMPDELTA TimeSlice shall contain at least operationalStatus.
				Name: <value-of select="name()" />
				ID: <value-of select="$featureId" />
			</assert>
			
			<!--
				The TEMPDELTA TimeSlice of a NavaidEquipment associated with the Event cannot 
				have the values "FALSE_POSSIBLE" or "CONDITIONAL" for its aixm:operationalStatus. 
			 -->
			<assert id="dnes-4.10.5-2" test="not(contains('FALSE_POSSIBLE,CONDITIONAL',aixm:availability/aixm:NavaidOperationalStatus/aixm:operationalStatus))">
				The TEMPDELTA TimeSlice of a NavaidEquipment associated with the Event cannot have the values "FALSE_POSSIBLE" or "CONDITIONAL" for its aixm:operationalStatus. 
				ID: <value-of select="$featureId" />
			</assert>
			
			<!--
				The value "PARTIAL" can appear only in a TEMPDELTA TimeSlice of a TACAN 
				associated with the Event and only if the signalType has one of the 
				values: "AZIMUTH" or "DISTANCE".
			 -->
			<let name="testSignalType" value="'TACAN'=$elementName and $opStatus='PARTIAL'"/>
			<assert id="dnes-4.10.5-3" test="not($testSignalType) or ($testSignalType and contains('AZIMUTH,DISTANCE',aixm:availability/aixm:NavaidOperationalStatus/aixm:signalType))">
				The value "PARTIAL" can appear only in a TEMPDELTA TimeSlice of a TACAN associated with the Event and only if the signalType has one of the values: "AZIMUTH" or "DISTANCE". 
				ID: <value-of select="$featureId" />
			</assert>
			
			<!-- 
				If a VOR that is used (by xlink:href) as aixm:navaidEquipment for a Navaid 
				of type VOR/DME has a TEMPDELTA TimeSlice with aixm:operationalStatus with 
				one of the values UNSERVICEABLE, ONTEST, INTERRUPT, FALSE_INDICATION, 
				DISPLACED, OTHER or IN_CONSTRUCTION, then the VOR/DME Navaid shall also have 
				a TEMPDELTA TimeSlice with identical validity time, aixm:type="DME" and 
				aixm:operationalStatus according to the mapping table of the rule ER-09
			 -->
			<let name="testVOR" value="'VOR'=$elementName and $navaidElement and 'VOR/DME'=$navaidElement/aixm:type and contains('UNSERVICEABLE,ONTEST,INTERRUPT,FALSE_INDICATION,DISPLACED,OTHER,IN_CONSTRUCTION',$navaidOpStatus)"/>
			<assert id="dnes-4.10.5-4" test="not($testVOR) or ($testVOR and aixm:timeSlice/aixm:VORTimeSlice/gml:validTime=$navaidElement/descendant::aixm:validTime)">
				Navaid VOR equipment present and associated to a Navaid, but TEMPDELTA time validity doesn't match.  
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.10.5-5" test="not($testVOR) or ($testVOR and aixm:type='DME')">
				Navaid VOR equipment present and associated to a Navaid, but that Navaid is not of type 'DME'.  
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.10.5-6" test="not($testVOR) or ($testVOR and $navaidOpStatus=$opStatus)">
				Navaid VOR equipment present and associated to a Navaid, but TEMPDELTA operational status doesn't match.  
				ID: <value-of select="$featureId" />
			</assert>
			
			<!-- 
			 	If a DME that is used (by xlink:href) as aixm:navaidEquipment for a Navaid 
			 	of type VOR/DME has a TEMPDELTA TimeSlice with aixm:operationalStatus with 
			 	one of the values UNSERVICEABLE, ONTEST, INTERRUPT, FALSE_INDICATION, 
			 	DISPLACED, OTHER or IN_CONSTRUCTION, then the VOR/DME Navaid shall also have 
			 	a TEMPDELTA TimeSlice with identical validity time, aixm:type="VOR" and 
			 	aixm:operationalStatus according to the mapping table of the rule ER-09
			 -->
			<let name="testDME" value="'DME'=$elementName and $navaidElement and 'VOR/DME'=$navaidElement/aixm:type and contains('UNSERVICEABLE,ONTEST,INTERRUPT,FALSE_INDICATION,DISPLACED,OTHER,IN_CONSTRUCTION',$navaidOpStatus)"/>
			<assert id="dnes-4.10.5-7" test="not($testDME) or ($testDME and aixm:timeSlice/aixm:DMETimeSlice/gml:validTime=$navaidElement/descendant::aixm:validTime)">
				Navaid DME equipment present and associated to a Navaid, but TEMPDELTA time validity doesn't match.  
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.10.5-8" test="not($testDME) or ($testDME and aixm:type='VOR')">
				Navaid DME equipment present and associated to a Navaid, but that Navaid is not of type 'VOR'.  
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.10.5-9" test="not($testDME) or ($testDME and $navaidOpStatus=$opStatus)">
				Navaid DME equipment present and associated to a Navaid, but TEMPDELTA operational status doesn't match.  
				ID: <value-of select="$featureId" />
			</assert>
			  
			<!--
				If a Localizer that is used (by xlink:href) as aixm:navaidEquipment for a 
				Navaid of type ILS or ILS/DME has a TEMPDELTA TimeSlice with 
				aixm:operationalStatus with one of the values UNSERVICEABLE, ONTEST, 
				INTERRUPT, FALSE_INDICATION, DISPLACED, OTHER or IN_CONSTRUCTION, then the 
				ILS or ILS/DME Navaid shall also have a TEMPDELTA TimeSlice with identical 
				validity time, aixm:type="OTHER" and aixm:operationalStatus according to 
				the mapping table of the rule ER-09
			 -->
			<let name="testLocalizer" value="'Localizer'=$elementName and $navaidElement and contains('ILS,ILS/DME',$navaidElement/aixm:type) and contains('UNSERVICEABLE,ONTEST,INTERRUPT,FALSE_INDICATION,DISPLACED,OTHER,IN_CONSTRUCTION',$navaidOpStatus)"/>
			<assert id="dnes-4.10.5-10" test="not($testLocalizer) or ($testLocalizer and aixm:timeSlice/aixm:DMETimeSlice/gml:validTime=$navaidElement/descendant::aixm:validTime)">
				Navaid Localizer equipment present and associated to a Navaid, but TEMPDELTA time validity doesn't match.  
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.10.5-11" test="not($testLocalizer) or ($testLocalizer and aixm:type='OTHER')">
				Navaid Localizer equipment present and associated to a Navaid, but that Navaid is not of type 'OTHER'.  
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.10.5-12" test="not($testLocalizer) or ($testLocalizer and $navaidOpStatus=$opStatus)">
				Navaid Localizer equipment present and associated to a Navaid, but TEMPDELTA operational status doesn't match.  
				ID: <value-of select="$featureId" />
			</assert>
			  
			<!--
				 If a Glidepath that is used (by xlink:href) as aixm:navaidEquipment for a 
				 Navaid of type ILS or ILS/DME has a TEMPDELTA TimeSlice with 
				 aixm:operationalStatus with one of the values UNSERVICEABLE, ONTEST, 
				 INTERRUPT, FALSE_INDICATION, DISPLACED, OTHER or IN_CONSTRUCTION, then the 
				 ILS or ILS/DME Navaid shall also have a TEMPDELTA TimeSlice with identical
				 validity time, aixm:type="LOC" or "LOC_DME" and aixm:operationalStatus 
				 according to the mapping table of the rule ER-09
			 -->
			<let name="testGlidepath" value="'Glidepath'=$elementName and $navaidElement and contains('ILS,ILS/DME',$navaidElement/aixm:type) and contains('UNSERVICEABLE,ONTEST,INTERRUPT,FALSE_INDICATION,DISPLACED,OTHER,IN_CONSTRUCTION',$navaidOpStatus)"/>
			<assert id="dnes-4.10.5-13" test="not($testGlidepath) or ($testGlidepath and aixm:timeSlice/aixm:DMETimeSlice/gml:validTime=$navaidElement/descendant::aixm:validTime)">
				Navaid Glidepath equipment present and associated to a Navaid, but TEMPDELTA time validity doesn't match.  
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.10.5-14" test="not($testGlidepath) or ($testGlidepath and contains('LOC,LOC_DME',aixm:type))">
				Navaid Glidepath equipment present and associated to a Navaid, but that Navaid is not of type 'OTHER'.  
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.10.5-15" test="not($testGlidepath) or ($testGlidepath and $navaidOpStatus=$opStatus)">
				Navaid Glidepath equipment present and associated to a Navaid, but TEMPDELTA operational status doesn't match.  
				ID: <value-of select="$featureId" />
			</assert>
			
			 
			<!--
				 If the Navaid or NavaidEquipment TEMPDELTA TimeSlice includes an 
				 aixm:availability, then there should not exist any other TEMPDELTA for the 
				 same Navaid or NavaidEquipment that also includes an aixm:availability 
				 element.
			 -->
			<let name="navaidCount" value="count(//*[@gml:id=$featureId]/aixm:timeSlice/*[aixm:interpretation='TEMPDELTA']/aixm:availability)"/>
			<assert id="dnes-4.10.5-16" test="1 = $navaidCount">
				If the Navaid or NavaidEquipment TEMPDELTA TimeSlice includes an 
				aixm:availability, then there should not exist any other TEMPDELTA for the 
				same Navaid or NavaidEquipment that also includes an aixm:availability 
				element.
				There are <value-of select="$navaidCount" /> with ID: <value-of select="$featureId" /> and an availability element.
			</assert>
			
		</rule>
	</pattern>

	<!-- Scenario: Other Event - OTHER -->
	<pattern name="Digital NOTAM events - Scenarion: Other Event">
		<!-- Rule: Event for this scenario -->
		<rule context="//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'OTHER')]">
			<!-- At least one feature must be present and associated to an Event of scenario OTHER -->
			<assert id="dnes-er-4.15.2-1" test="concat('#',@gml:id) = //aixm:extension/*/event:theEvent/@xlink:href">
				An event of scenario OTHER must include at least one associated feature.
				Event ID: <value-of select="@gml:id" />
			</assert>

			<!--
				OTHER events must have an encoding of ANNOTATION.
			 -->
			<assert id="dnes-4.15.2-1" test="event:timeSlice/event:EventTimeSlice/event:encoding = 'ANNOTATION'">
				OTHER events must have an encoding of ANNOTATION.
				Event ID: <value-of select="@gml:id" />
			</assert>

			<!--
				OTHER events must include a textNOTAM/NOTAM/text element (not NIL).
			 -->
			<assert id="dnes-4.15.2-2" test="event:timeSlice/event:EventTimeSlice/event:textNOTAM/event:NOTAM/event:text">
				OTHER events must include a textNOTAM/NOTAM/text element (not NIL).
				Event ID: <value-of select="@gml:id" />
			</assert>
		</rule>
		
		<!-- Rule: TEMPDELTA time slice of any feature associated to this event -->
		<rule context="//aixm:timeSlice[descendant::event:theEvent/@xlink:href = concat('#',//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'OTHER')]/@gml:id)][*/aixm:interpretation = 'TEMPDELTA']/child::*">
			<let name="featureId" value="../../@gml:id"/>
			<let name="elementName" value="local-name(../..)"/>
			<let name="eventId" value="substring(descendant::event:theEvent/@xlink:href, 2)"/>
			<let name="eventFeature" value="//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'OTHER')][@gml:id = $eventId]"/>

			<!--
				All features that are associated to the OTHER event will 
				include a note of purpose WARNING with matching text to 
				the textNOTAM/NOTAM/text element of the event.
			 -->
			<assert id="dnes-4.15.2-3" test="aixm:annotation/aixm:Note/aixm:purpose = 'WARNING'">
				Features associated to an event of secnario OTHER must include an annotation/Note of purpose WARNING.
				Name: <value-of select="name()" />
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.15.2-4" test="aixm:annotation/aixm:Note/aixm:translatedNote/aixm:LinguisticNote/aixm:note = $eventFeature/event:timeSlice/event:EventTimeSlice/event:textNOTAM/event:NOTAM/event:text">
				Features associated to an event of secnario OTHER must include an annotation/Note that matches the textNOTAM/NOTAM/text of the event.
				Name: <value-of select="name()" />
				ID: <value-of select="$featureId" />
			</assert>
			
		</rule>
	</pattern>

	<!-- Scenario: Runway Closure - RWY.CLS -->
	<pattern name="Digital NOTAM events - Scenarion: Runway Closure">
		<!-- Rule: Event for this scenario -->
		<rule context="//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'RWY.CLS')]">
			<!-- At least one RunwayDirection must be present and associated to an Event of scenario RWY.CLS -->
			<assert id="dnes-er-4.9-1" test="concat('#',@gml:id) = (//aixm:RunwayDirection)/descendant::aixm:extension/*/event:theEvent/@xlink:href">
				A Runway Closure scenario must include at least one RunwayDirection.
				Event ID: <value-of select="@gml:id" />
			</assert>
		</rule>
		
		<!-- Rule: TEMPDELTA time slice of any runway direction associated to this event -->
		<rule context="//aixm:timeSlice[../../aixm:RunwayDirection][descendant::event:theEvent/@xlink:href = concat('#',//event:Event[starts-with(event:timeSlice/event:EventTimeSlice/event:scenario,'RWY.CLS')]/@gml:id)][*/aixm:interpretation = 'TEMPDELTA']/child::*">
			<let name="featureId" value="../../@gml:id"/>
			<let name="elementName" value="local-name(../..)"/>
			<let name="opStatus" value="aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:operationalStatus"/>
			<let name="eventId" value="substring(descendant::event:theEvent/@xlink:href, 2)"/>

			<!--
				As a minimum, in addition to the AIXM mandatory properties gml:validTime 
				and aixm:interpretation, each RunwayDirection TEMPDELTA TimeSlice shall 
				contain at least aixm:sequenceNumber and one aixm:availability element 
				with at least the aixm:operationalStatus descendant element specified 
				(not NIL).
			 -->
			<assert id="dnes-4.9.5-1" test="aixm:sequenceNumber">
				The RunwayDirection TEMPDELTA TimeSlice shall contain a sequenceNumber.
				Name: <value-of select="name()" />
				ID: <value-of select="$featureId" />
			</assert>
			<assert id="dnes-4.9.5-2" test="$opStatus">
				The RunwayDirection TEMPDELTA TimeSlice shall contain an operationalStatus.
				Name: <value-of select="name()" />
				ID: <value-of select="$featureId" />
			</assert>
			
			<!-- 
				For the ManoeuvringAreaAvailability with operationalStatus=CLOSED 
				included in the TEMPDELTA, if aixm:ManoeuvringAreaUsage.operations=ALL, 
				then aixm:priorPermission and/or aixm:type shall be specified (not NIL)
			-->
			<let name="testClosedAll" value="$opStatus='CLOSED' and aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:operation='ALL'"/>
			<assert id="dnes-4.9.5-3" test="not($testClosedAll) or ($testClosedAll and (aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:type or aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:priorPermission))">
				If the Manoevuring Area TEMPDELTA is CLOSED for ALL operations, then either priorPermission or type must appear in the usage
				ID: <value-of select="$featureId" />
			</assert>

			<!-- 
				If aixm:ManoeuvringAreaUsage.priorPermission is specified, then the 
				aixm:ManoeuvringAreaUsage.type shall be "CONDITIONAL"
			 -->
			<assert id="dnes-4.9.5-4" test="not(aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:priorPermission) or (aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:priorPermission and aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage[aixm:type='CONDITIONAL'])">
				If aixm:ManoeuvringAreaUsage.priorPermission is specified, then the aixm:ManoeuvringAreaUsage.type shall be "CONDITIONAL"
				ID: <value-of select="$featureId" />
			</assert>

			<!-- 
				For the ManoeuvringAreaAvailability with operationalStatus=CLOSED 
				included in the TEMPDELTA, any eventual child 
				aixm:ManoeuvringAreaUsage.type cannot have any other type than 
				"PERMIT" or "CONDITIONAL".
			 -->
			<assert id="dnes-4.9.5-5" test="not($opStatus='CLOSED') or ($opStatus='CLOSED' and contains('CONDITIONAL,PERMIT',aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:type))">
				For the ManoeuvringAreaAvailability with operationalStatus=CLOSED included in the TEMPDELTA, any eventual child aixm:ManoeuvringAreaUsage.type cannot have any other type than "PERMIT" or "CONDITIONAL".
				ID: <value-of select="$featureId" />
			</assert>
			
			<!-- 
				If aixm:ManoeuvringAreaUsage.type is specified (not NIL), then at least one aixm:selection shall be specified (not NIL)
			-->
			<assert id="dnes-4.9.5-6" test="not(aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:type) or (aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:type and aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:selection)">
				If aixm:ManoeuvringAreaUsage.type is specified (not NIL), then at least one aixm:selection shall be specified (not NIL)
				ID: <value-of select="$featureId" />
			</assert>
			
			<!-- 
				Only the following properties of AircraftCharacteristics can be used in this 
				scenario: type, engine, wingSpan and wingSpanInterpretation, weight and 
				weightInterpretation 
			-->
			<assert id="dnes-4.9.5-7" test="not(aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:selection/descendant::aixm:AircraftCharacteristic[not(aixm:type | aixm:engine | aixm:wingSpan | aixm:wingSpanInterpretation | aixm:weight | aixm:weightInterpretation)])">
				Only the following properties of AircraftCharacteristics can be used in this scenario: type, engine, wingSpan and wingSpanInterpretation, weight and weightInterpretation.
				Name: <value-of select="name(aixm:availability/aixm:ManoeuvringAreaAvailability/aixm:usage/aixm:ManoeuvringAreaUsage/aixm:selection/descendant::aixm:AircraftCharacteristic/*)" />
				ID: <value-of select="$featureId" />
			</assert>

			<!-- 
				If more than one RunwayDirection has a TEMPDELTA TimeSlice associated with the 
				Event (the runway itself is closed), then these TEMPDELTA shall have identical 
				ManoeuvringAreaAvailability child elements. This rule concerns only the 
				ManoeuvringAreaAvailability elements that are not copied from the BASELINE 
				data - they do not have operationalStatus=NORMAL and do not have an associated 
				annotation with purpose=REMARK and the text="Baseline data copy. Not included 
				in the NOTAM text generation".
			 -->
			 <!-- untestable in schematron due to lack of support for aggregation features in xpath -->
			<!-- 
			<let name="startTime" value="aixm:timeInterval/aixm:Timesheet/aixm:startTime"/>
			<assert id="dnes-4.9.5-8" test="count(//aixm:ManoeuvringAreaAvailability[../../../aixm:RunwayDirectionTimeSlice][../../descendant::event:theEvent/@xlink:href = concat('#', $eventId)][../../../*/aixm:interpretation = 'TEMPDELTA']) != count(//aixm:ManoeuvringAreaAvailability[../../../aixm:RunwayDirectionTimeSlice][../../descendant::event:theEvent/@xlink:href = concat('#', $eventId)][../../../*/aixm:interpretation = 'TEMPDELTA'][aixm:timeInterval/aixm:Timesheet/aixm:startTime=$startTime])">
				Only the following properties of AircraftCharacteristics can be used in this scenario: type, engine, wingSpan and wingSpanInterpretation, weight and weightInterpretation.
				Count: <value-of select="count(//aixm:ManoeuvringAreaAvailability[../../../aixm:RunwayDirectionTimeSlice][../../descendant::event:theEvent/@xlink:href = '#event.runway_closure_example'][../../../*/aixm:interpretation = 'TEMPDELTA'])" />
				ID: <value-of select="$featureId" />
			</assert>
			 -->
		</rule>
	</pattern>

</schema>
