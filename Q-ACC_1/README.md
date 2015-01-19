# ACC_1 #
----------

###General###

|------------------------------|---------------------------------------------------------------|
|**Name:**                     |ACC_1 							       |
|**Description:**              |Validate RDF files. 					       |
|**DPU class name:**           |ACC1     						       | 
|**Configuration class name:** |ACC1Config_V1                           		       |
|**Dialogue class name:**      |ACC1VaadinDialog 					       |


###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Target path:*** 	          |Path and target CSV file name.          |
|**Host:**		          |Main domain where RDFAlerts runs.  	   |
|**Port:**		          |Port where RDFAlerts works.  	   |
|**Path:**			  |Path where RDFAlerts is installed.      |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                          |
|--------------------|-----------|---------------------------------|-------------------------------------|
|input  	     |i 	 |FilesDataUnit  		   |File with .ttl, .nt, .rdf, ecc. extension.  |
|output 	     |o 	 |WritableFilesDataUnit            |CSV file containing errors detected. |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.0.0              |RDFAlerts must be installed into Tomcat.        |                                

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   |
