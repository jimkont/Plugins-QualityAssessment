# Q-MC #
----------

###General###

|                              |                                                                                                                                                         |
|------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
|**Name:**                     |Q-MC 		                     					                                                                                                                   |
|**Description:**              |A DPU that assesses the metadata completeness. The assessment refers to the presence or not of the metadata considered under the completeness dimension. |
|**DPU class name:**           |MC     						                                                                                                                                       |
|**Configuration class name:** |MCConfig_V1                           		                                                                                                               |
|**Dialogue class name:**      |MCVaadinDialog 					                                                                                                                                 |


###Configuration parameters###


|Parameter                     |Description                   |
|------------------------------|------------------------------|
|**Subject Type URI:** 	       |Type of resource.             |
|**Property:**		             |Property to check.            |

***

### Inputs and outputs ###

|Name              |Type     |DataUnit                     |Description          |
|------------------|---------|-----------------------------|---------------------|
|input  	         |i 	     |RDFDataUnit 		             |RDF graph.			     |
|output 	         |o 	     |WritableRDFDataUnit 	       |RDF Quality graph.   |

***

### Version history ###

|Version            |Release notes        |
|-------------------|---------------------|
|1.0.0              |N/A                  |

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   |