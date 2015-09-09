### Description

Checks that literal value for the given predicate and given class of subjects respects certain regular expression such as the postal address, the phone number, etc. A user specifies the RDF class, the RDF property for which he would like to verify the literal correctness through a regular expression that can be either selected from a default list or can be inserted directly by the user. The output is the RDF quality report providing a normalised score in the range 0-1 (low= a few values do not satisfy the regular expression, high = most of the values do not satisfy the regular expression) (Comsode DPU ID: Q-ACC6)

### Configuration parameters

| Name | Description |
|:----|:----|
|**Subject Class URI** 	   |Class of subjects for which property value is checked.       |
|**Property URI**		       |Property to be checked.           	       |
|**RegularExpression**		   |Regular expression to be checked.  |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input |i |RDFDataUnit |RDF graph. |x|
|output |o |WritableRDFDataUnit |RDF quality graph. |x|
