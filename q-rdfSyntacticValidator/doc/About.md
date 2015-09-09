### Description

Validates an RDF document by identifying undefined classes/properties with respect to the underlying vocabularies, the usage of incorrect literals with respect to their datatype and the usage of incorrect instances in case where a property is of owl:DatatypeProperty/owl:ObjectProperty.  To use the DPU, RDFAlerts WAR file must be installed into Tomcat. The WAR file is available from: http://aidanhogan.com/misc/RDFAlerts.war. Just deploy and run it directly in Tomcat 5 or newer. (Comsode DPU ID: Q-ACC1, covers also the quality metrics  Q-ACC2 and Q-ACC7)

### Configuration parameters

| Name | Description |
|:----|:----|
|**RDFAlerts' host**		                  |Host where RDFAlerts runs.  	   |
|**RDFAlerts' port**		                  |Port where RDFAlerts works.  	       |
|**RDFAlerts' path**			              |Path where RDFAlerts is installed withing the application server.      |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input |i |RDFDataUnit |RDF graph to evaluate. |x|
|output |o |WritableRDFDataUnit |RDF quality graph. |x|
