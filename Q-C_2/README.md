# Q-C_2 #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |C_2 							                               |
|**Description:**              |Check the missing elements from a specified list of subject/property. |
|**DPU class name:**           |C2     						                                   |
|**Configuration class name:** |C2Config_V1                           		                   |
|**Dialogue class name:**      |C2VaadinDialog 					                               |


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
|N/A               |N/A                   | 
