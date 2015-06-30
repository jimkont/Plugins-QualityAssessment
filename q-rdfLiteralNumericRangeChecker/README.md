# Q-RdfLiteralNumericRangeChecker #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |Q-RdfLiteralNumericRangeChecker 							                               |
|**Description:**              |Check the incorrectness with respect to the specified range. A user should specify the RDF class, the RDF property for which he would like to verify if the values are in the specified range determined by the user. The range is specified by the user by indicating the lower and the upper bound of the value. The output is an RDF graph based on DaQ vocabulary and provides a normalised score in the range 0-1 (low= a few values are not in the specified range, high = most of the values are not in the specified range)                 |
|**DPU class name:**           |NumericalRangeChecker     						                               |
|**Configuration class name:** |NumericalRangeCheckerConfig_V1                           		               |
|**Dialogue class name:**      |NumericalRangeCheckerVaadinDialog 					                           |


###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**ClassURI:** 	                  |ClassURI linked to the property.        |
|**PropertyURI:**		          |Property to check.           	       |
|**LowerBound:**                  |Upper bound to evaluate.                |
|**UpperBound:**                  |Lower bound to evaluate.                |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                          |
|--------------------|-----------|---------------------------------|-------------------------------------|
|input  	         |i 	     |RDFDataUnit    		           |RDF graph.			                 |
|output 	         |o 	     |WritableRDFDataUnit              |RDF Quality graph.                   |

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
