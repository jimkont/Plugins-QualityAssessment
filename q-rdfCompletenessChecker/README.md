# Q-RdfCompletenessChecker #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |Q-RdfCompletenessChecker 							                               |
|**Description:**              |Checks the missing object values for the given predicate and given class of subjects. A user specifies the RDF class and the RDF predicate, then the DPU checks for each pair whether instances of the given RDF class contain the specified RDF predicate. At the end, DPU computes completeness score for each class and predicate normalised to a range 0-1 and produces report. (Comsode DPU ID: Q-C2)|
|**DPU class name:**           |CompletenessChecker     						                                   |
|**Configuration class name:** |CompletenessCheckerConfig_V1                           		                   |
|**Dialogue class name:**      |CompletenessCheckerVaadinDialog 					                               |


###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Subject Class URI:** 	                  |Class of subjects for which property value is checked.         |
|**Property URI:**		              |Property to be checked.           	       |

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
|AndreAga          |QualityOntology library is required. (https://github.com/UnifiedViews/Plugins-QualityAssessment/tree/master/qualityOntology) | 
