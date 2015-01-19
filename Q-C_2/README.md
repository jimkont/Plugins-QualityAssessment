# C_2 #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |C_2 							       |
|**Description:**              |Checks if the given properties have their own object.          |
|**DPU class name:**           |C2     						       | 
|**Configuration class name:** |C2Config_V1                           		       |
|**Dialogue class name:**      |C2VaadinDialog 					       |


###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Subject:*** 	                  |Subject linked to the property.         |
|**Property:**		          |Property to check.           	   |
|**FileName:**		          |Path and target CSV file name.  	   |
|**Path:**			  |Path to use in case of test.            |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                          |
|--------------------|-----------|---------------------------------|-------------------------------------|
|input  	     |i 	 |RDFDataUnit    		   |RDF graph.			         |
|output 	     |o 	 |WritableFilesDataUnit            |CSV file containing the level of completeness for each property. |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.0.0              |N/A        |                                

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 
