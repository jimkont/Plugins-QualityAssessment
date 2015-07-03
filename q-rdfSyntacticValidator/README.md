# Q-RdfSyntacticValidator #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |Q-RdfSyntacticValidator 		                     					       |
|**Description:**              |Validates an RDF document by identifying undefined classes/properties with respect to the underlying vocabularies, the usage of incorrect literals with respect to their datatype and the usage of incorrect instances in case where a property is of owl:DatatypeProperty/owl:ObjectProperty.  To use the DPU, RDFAlerts WAR file must be installed into Tomcat. The WAR file is available from: http://aidanhogan.com/misc/RDFAlerts.war. Just deploy and run it directly in Tomcat 5 or newer. (Comsode DPU ID: Q-ACC1, covers also the quality metrics  Q-ACC2 and Q-ACC7 ) |
|**DPU class name:**           |RDFValidator     						                               |
|**Configuration class name:** |RDFValidatorConfig_V1                           		               |
|**Dialogue class name:**      |RDFValidatorVaadinDialog 					                           |


###Configuration parameters###


|Parameter                        |Description                             |
|---------------------------------|----------------------------------------|
|**RDFAlerts' host:**		                  |Host where RDFAlerts runs.  	   |
|**RDFAlerts' port:**		                  |Port where RDFAlerts works.  	       |
|**RDFAlerts' path:**			              |Path where RDFAlerts is installed withing the application server.      |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                          |
|--------------------|-----------|---------------------------------|-------------------------------------|
|input  	         |i      	 |RDFDataUnit  		               |RDF graph to evaluate.               |
|output 	         |o 	     |WritableRDFDataUnit              |RDF quality graph.                   |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.0.0              |N/A|

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|AndreAga          |QualityOntology library is required. (https://github.com/UnifiedViews/Plugins-QualityAssessment/tree/master/qualityOntology) |  
|AndreAga          |Limitations: RDFAlerts supports only N3 format as RDF format. So all other formats have to be converted to N3. The graph itself must be converted to N3. RDFAlerts works with POST request which is limited to 2Gb of content, so all dataset bigger than 2Gb will produce an error. |
|AndreAga          |Problems: This dpu takes in input a graph so if the rdf file has problems, which don't allow to FilesToRdf DPU to work properly, they won't be found by RDFAlerts, because they must be correct in order to produce the rdf graph. On the other hand if this dpu takes in input an rdf files and its content shows datatype errors, the conversion to N3 would skip all these errors by removing relative triples from the final N3 content. The result will be that RDFAlerts will never find such errors because they aren't in the content passed to the tool.  |
