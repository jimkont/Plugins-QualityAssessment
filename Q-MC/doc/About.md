### Description

Checks the missing metadata values for a given list of predicates which link the metadata to a dataset. A user specifies the RDF predicate, then the DPU checks for each pair whether the specified predicate is present (return 1) or not present (return 0). The DPU computes the metadata completeness score for the specified list of predicates normalised to a range 0-1. (Comsode DPU ID: Q-MC)

### Configuration parameters

| Name | Description |
|:----|:----|
|**Property** | Property to check.|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input |i |RDFDataUnit |RDF graph. |x|
|output |o |WritableRDFDataUnit |RDF Quality graph. |x|
