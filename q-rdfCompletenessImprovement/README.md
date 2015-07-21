# Q-RdfCompletenessImprovement #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |Q-RdfCompletenessImprovement 							                               |
|**Description:**              |Given the source dataset and the reference dataset, improve the source dataset by adding the missing values of the resources based on the reference dataset. The dpu gets as input the similarity between resources of the two datasets and also the two datasets. In case when a property of a resource is missing in the source dataset but available for a similar resource in the reference dataset, add the new property and the corresponding value in the source dataset. The DPU returns the file completed for each resource in the source dataset according to the reference dataset.|
|**DPU class name:**           |CompletenessImprovement    						                                   |
|**Configuration class name:** |CompletenessImprovementConfig_V1                           		                   |
|**Dialogue class name:**      |CompletenessImprovementVaadinDialog 					                               |


###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Source Property URI:** 	      |Property that has a correspondence in the target dataset .         |
|**Target Property URI:**		  |Property that has a correspondence in the source dataset.           	       |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                          |
|--------------------|-----------|---------------------------------|-------------------------------------|
|input_source  	     |i 	     |RDFDataUnit    		           |RDF source graph.			                 |
|input_target  	     |i 	     |RDFDataUnit    		           |RDF target graph.			                 |
|input_silk  	     |i 	     |RDFDataUnit    		           |RDF silk linker graph.			                 |
|output 	         |o 	     |WritableRDFDataUnit              |RDF graph containing the improved source graph. |

***

### Version history ###

|Version            |Release notes        |
|-------------------|---------------------|
|1.0.0              |N/A                  |

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|AndreAga          |QualityOntology library is required. (https://github.com/UnifiedViews/Plugins-QualityAssessment/tree/master/qualityOntology) | 
