# Q-CU_1 #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |CU_1 							                               |
|**Description:**              |Checks out-of-date data.                                       |
|**DPU class name:**           |CU1     						                               |
|**Configuration class name:** |CU1Config_V1                           		                   |
|**Dialogue class name:**      |CU1VaadinDialog 					                           |


###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**FileName:**		              |CSV output filename.  	               |
|**Path:**			              |Path to use in case of test.            |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                          |
|--------------------|-----------|---------------------------------|-------------------------------------|
|input  	         |i 	     |RDFDataUnit    		           |RDF matadata graph (metadata.trig / metadata.ttl). |
|output 	         |o 	     |WritableFilesDataUnit            |CSV file containing the currency of the dataset. |

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