# Q-RdfResourceUnderstandability #
----------

###General###

|                              |                                                                                               |
|------------------------------|-----------------------------------------------------------------------------------------------|
|**Name:**                     |Q-RdfResourceUnderstandability 							                                                               |
|**Description:**              |Understandability of a resource is measured as the presence of human readable labels and descriptions of resources.|
|**DPU class name:**           |ResourceUnderstandability     						                                                               |
|**Configuration class name:** |ResourceUnderstandabilityConfig_V1                           		                                               |
|**Dialogue class name:**      |ResourceUnderstandabilityVaadinDialog                                      					                       |


###Configuration parameters###


|Parameter                     |Description                   |
|------------------------------|------------------------------|
|**Resource:** 	               |Type of resource.             |
|**Property:**		           |Property to check.            |

***

### Inputs and outputs ###

|Name              |Type     |DataUnit                     |Description          |
|------------------|---------|-----------------------------|---------------------|
|input  	       |i 	     |RDFDataUnit 		           |RDF graph.			 |
|output 	       |o 	     |WritableRDFDataUnit 	       |RDF Quality graph.   |

***

### Version history ###

|Version            |Release notes        |
|-------------------|---------------------|
|1.0.0              |N/A                  |

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|AndreAga          |QualityOntology library is required. (https://github.com/UnifiedViews/Plugins-QualityAssessment/tree/develop/q-qualityOntology) | 
