# Q-LC #
----------

###General###

|                              |                                                                                                                                                         |
|------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
|**Name:**                     |Q-LC		                     					                                                                                                                   |
|**Description:**              |A DPU that assesses the licensing. The assessment
                                refers to the presence or not of the metadata considered under 
                                the licensing dimension |
|**DPU class name:**           |LC     						                                                                                                                                       |
|**Configuration class name:** |LCConfig_V1                           		                                                                                                               |
|**Dialogue class name:**      |LCVaadinDialog 					                                                                                                                                 |


###Configuration parameters###


|Parameter                     |Description                   |
|------------------------------|------------------------------|
|**empty**		           |empty            |


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