### Description

Checks the incorrect numeric range for the given predicate and given class of subjects. A user should specify the RDF class, the RDF property for which he would like to verify if the values are in the specified range determined by the user. The range is specified by the user by indicating the lower and the upper bound of the value. The output is the RDF quality report providing a normalised score in the range 0-1 (low= a few values are not in the specified range, high = most of the values are not in the specified range) (Comsode DPU ID: Q-ACC4)

### Configuration parameters

| Name | Description |
|:----|:----|
|**Subject Class URI** 	                  |Class of subjects for which property value is checked.       |
|**Property URI**		          |Property to be checked.           	       |
|**LowerBound**                  |Upper bound to evaluate.                |
|**UpperBound**                  |Lower bound to evaluate.                |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input |i |RDFDataUnit |RDF graph. |x|
|output |o |WritableRDFDataUnit |RDF quality graph. |x|
