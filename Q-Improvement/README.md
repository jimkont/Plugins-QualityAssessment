# Q-Improvement #
----------

###General###

|                              |                                                                                    |
|------------------------------|------------------------------------------------------------------------------------|
|**Name:**                     |Q-Improvement 							                                                |
|**Description:**              |Improve the quality of dataset..                                                    |
|**DPU class name:**           |Improvement     						                                                |
|**Configuration class name:** |ImprovementConfig_V1                           		                                    |
|**Dialogue class name:**      |ImprovementVaadinDialog                                      					        |


###Configuration parameters###


|Parameter                     |Description                   |
|------------------------------|------------------------------|
|N/A 	                       |N/A    |

***

### Inputs and outputs ###

|Name              |Type     |DataUnit                     |Description          |
|------------------|---------|-----------------------------|---------------------|
|input  	       |i 	     |FilesDataUnit 		       |RDF file.			 |
|output 	       |o 	     |WritableFilesDataUnit 	   |RDF improved file.   |

***

### Version history ###

|Version            |Release notes        |
|-------------------|---------------------|
|1.0.0              |  |

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|AndreAga          |This DPU needs an efficient pattern matching algorithm to find all URI occurrences in dataset. |