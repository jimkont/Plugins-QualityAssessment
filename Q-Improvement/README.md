# Q-RDFUnit #
----------

###General###

|                              |                                                                                    |
|------------------------------|------------------------------------------------------------------------------------|
|**Name:**                     |Q-RDFUnit 							                                                |
|**Description:**              |Check the validity of a dataset.                                                    |
|**DPU class name:**           |RDFUnit     						                                                |
|**Configuration class name:** |RDFUnitConfig_V1                           		                                    |
|**Dialogue class name:**      |RDFUnitVaadinDialog                                      					        |


###Configuration parameters###


|Parameter                     |Description                   |
|------------------------------|------------------------------|
|**Prefix:** 	               |Prefix of custom ontology used in the dataset.    |
|**URI:**		               |URI of the ontology, expressed with the final '#'.|
|**URL:**		               |URL to download the ontology file.                |

***

### Inputs and outputs ###

|Name              |Type     |DataUnit                     |Description          |
|------------------|---------|-----------------------------|---------------------|
|input  	       |i 	     |FilesDataUnit 		       |RDF file.			 |
|output 	       |o 	     |WritableRDFDataUnit 	       |RDF Quality graph.   |

***

### Version history ###

|Version            |Release notes        |
|-------------------|---------------------|
|1.0.0              |RDFUnit tool must be installed in order to compile this DPU. |

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   |