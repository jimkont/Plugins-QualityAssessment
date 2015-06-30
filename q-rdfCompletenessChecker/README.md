# Q-RdfCompletenessChecker #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |Q-RdfCompletenessChecker 							                               |
|**Description:**              |Check the missing elements from a specified list of subject/property. A user should specify the RDF class and the RDF predicate, then the DPU checks for each pair whether instances of the RDF class contain the specified RDF predicate. At the end, DPU computes completeness score for each class and predicate normalised in a range 0-1. |
|**DPU class name:**           |CompletenessChecker     						                                   |
|**Configuration class name:** |CompletenessCheckerConfig_V1                           		                   |
|**Dialogue class name:**      |CompletenessCheckerVaadinDialog 					                               |


###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Subject:** 	                  |Subject linked to the property.         |
|**Property:**		              |Property to check.           	       |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                          |
|--------------------|-----------|---------------------------------|-------------------------------------|
|input  	         |i 	     |RDFDataUnit    		           |RDF graph.			                 |
|output 	         |o 	     |WritableFilesDataUnit            |RDF quality graph containing the level of completeness for each property. |

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
