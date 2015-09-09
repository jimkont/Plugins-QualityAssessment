Q-RdfSyntacticValidator
----------

### Documentation

* see [Plugin Documentation](./doc/About.md)

### Technical notes

* QualityOntology library is required. (https://github.com/UnifiedViews/Plugins-QualityAssessment/tree/master/qualityOntology)
* Limitations: RDFAlerts supports only N3 format as RDF format. So all other formats have to be converted to N3. The graph itself must be converted to N3. RDFAlerts works with POST request which is limited to 2Gb of content, so all dataset bigger than 2Gb will produce an error.
* Problems: This dpu takes in input a graph so if the rdf file has problems, which don't allow to FilesToRdf DPU to work properly, they won't be found by RDFAlerts, because they must be correct in order to produce the rdf graph. On the other hand if this dpu takes in input an rdf files and its content shows datatype errors, the conversion to N3 would skip all these errors by removing relative triples from the final N3 content. The result will be that RDFAlerts will never find such errors because they aren't in the content passed to the tool.

### Version history

* see [Changelog](./CHANGELOG.md)