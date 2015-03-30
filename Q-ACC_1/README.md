# Q-ACC_1 #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |Q-ACC_1 		                     					       |
|**Description:**              |Detecting syntax errors. Check the correctness of the literals, detection of ill-typed literals which do not abide by the lexical syntax for their respective datatype that can occur if a value is (i) malformed or (ii) is a member of an incompatible datatype. |
|**DPU class name:**           |ACC1     						                               |
|**Configuration class name:** |ACC1Config_V1                           		               |
|**Dialogue class name:**      |ACC1VaadinDialog 					                           |


###Configuration parameters###


|Parameter                        |Description                             |
|---------------------------------|----------------------------------------|
|**Host:**		                  |Main domain where RDFAlerts runs.  	   |
|**Port:**		                  |Port where RDFAlerts works.  	       |
|**Path:**			              |Path where RDFAlerts is installed.      |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                          |
|--------------------|-----------|---------------------------------|-------------------------------------|
|input  	         |i      	 |RDFDataUnit  		               |RDF graph to evaluate.               |
|output 	         |o 	     |WritableRDFDataUnit              |RDF Quality graph.                   |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.0.0              |RDFAlerts must be installed into Tomcat. <br>
                     The WAR file is available [here](http://aidanhogan.com/misc/RDFAlerts.war). <br>
                     Just deploy and run it directly in Tomcat 5 or newer. <br>
                     This DPU covers also the quality metrics required by the DPUs Q-ACC2 and Q-ACC7. <br>|

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   |
