# Q-RdfResourceUnderstandability #
----------

###General###

|                              |                                                                                               |
|------------------------------|-----------------------------------------------------------------------------------------------|
|**Name:**                     |Q-RdfResourceUnderstandability 							                                                               |
|**Description:**              |Check the understandability of resources as the presence of human readable labels and descriptions of resources. A user should specify the RDF class, the RDF property either by selecting it from a default list or by inserting a new property and the language tag which in turn can be selected by a default list or can be inserted by the user. The output is an RDF graph based on DaQ vocabulary and provides several normalised score in a range 0-1 (low= a few values do not have the language tag, high = most of the values do not have the language tag) each for every selected pair (RDF class and RDF property).|
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
