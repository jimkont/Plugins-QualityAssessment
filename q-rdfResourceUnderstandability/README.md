# Q-RdfResourceUnderstandability #
----------

###General###

|                              |                                                                                               |
|------------------------------|-----------------------------------------------------------------------------------------------|
|**Name:**                     |Q-RdfResourceUnderstandability 							                                                               |
|**Description:**              |Checks the understandability of a resource by checking for the given class of subjects the presence of the given predicate (e.g., human readable labels or descriptions of resources). A user should specify the RDF class, the RDF property either by selecting it from a default list or by inserting a new property and the language tag. The output is the RDF quality report providing normalised scores in a ranges 0-1 (low= a few values do not have the language tag, high = most of the values do not have the language tag)  for every pair of RDF class and RDF property.(Comsode DPU ID: Q-C5) |
|**DPU class name:**           |ResourceUnderstandability     						                                                               |
|**Configuration class name:** |ResourceUnderstandabilityConfig_V1                           		                                               |
|**Dialogue class name:**      |ResourceUnderstandabilityVaadinDialog                                      					                       |


###Configuration parameters###


|Parameter                     |Description                   |
|------------------------------|------------------------------|
|**Subject Class URI:** 	                  |Class of subjects for which property value is checked.       |
|**Property URI:**		          |Property to be checked.           	       |
|**Language tag:** 	                  |Language tag to be checked.       |

***

### Inputs and outputs ###

|Name              |Type     |DataUnit                     |Description          |
|------------------|---------|-----------------------------|---------------------|
|input  	       |i 	     |RDFDataUnit 		           |RDF graph.			 |
|output 	       |o 	     |WritableRDFDataUnit 	       |RDF quality graph.   |

***

### Version history ###

|Version            |Release notes        |
|-------------------|---------------------|
|1.0.0              |N/A                  |

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|AndreAga          |QualityOntology library is required. (https://github.com/UnifiedViews/Plugins-QualityAssessment/tree/master/qualityOntology) | 
