package eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class QualityOntology {

    public static final String QB = "http://purl.org/linked-data/cube#";

    public static final String DAQ = "http://purl.org/eis/vocab/daq#";

    public static final String EX = "http://comsode.eu/quality/";

    public static final URI QB_OBSERVATION;

    public static final URI QB_STRUCTURE;

    public static final URI DAQ_QUALITY_GRAPH;

    public static final URI DAQ_DSD;

    public static final URI DAQ_DIMENSION;

    public static final URI DAQ_HAS_METRIC;

    public static final URI DAQ_HAS_OBSERVATION;

    public static final URI DAQ_METRIC;

    public static final URI DAQ_COMPUTED_ON;

    public static final URI DAQ_VALUE;

    public static final URI DAQ_HAS_SEVERITY;

    public static final URI EX_TIMELINESS_DIMENSION;

    public static final URI EX_COMPLETENESS_DIMENSION;

    public static final URI EX_ACCURACY_DIMENSION;

    public static final URI EX_CONSISTENCY_DIMENSION;

    public static final URI EX_ACCURACY_NOTE;

    public static final URI EX_ACCURACY_WARNING;

    public static final URI EX_ACCURACY_ERROR;

    static {
        final ValueFactory factory = ValueFactoryImpl.getInstance();

        QB_OBSERVATION = factory.createURI(QB + "Observation");
        QB_STRUCTURE = factory.createURI(QB + "structure");

        DAQ_QUALITY_GRAPH = factory.createURI(DAQ + "QualityGraph");
        DAQ_DSD = factory.createURI(DAQ + "dsd");
        DAQ_DIMENSION = factory.createURI(DAQ + "Dimension");
        DAQ_HAS_METRIC = factory.createURI(DAQ + "hasMetric");
        DAQ_HAS_OBSERVATION = factory.createURI(DAQ + "hasObservation");
        DAQ_METRIC = factory.createURI(DAQ + "Metric");
        DAQ_COMPUTED_ON = factory.createURI(DAQ + "computedOn");
        DAQ_VALUE = factory.createURI(DAQ + "value");
        DAQ_HAS_SEVERITY = factory.createURI(DAQ + "severity");

        EX_TIMELINESS_DIMENSION = factory.createURI(EX + "timelinessDimension");
        EX_COMPLETENESS_DIMENSION = factory.createURI(EX + "completenessDimension");
        EX_ACCURACY_DIMENSION = factory.createURI(EX + "accuracyDimension");
        EX_CONSISTENCY_DIMENSION = factory.createURI(EX + "consistencyDimension");
        EX_ACCURACY_NOTE = factory.createURI(EX + "Note");
        EX_ACCURACY_WARNING = factory.createURI(EX + "Warning");
        EX_ACCURACY_ERROR = factory.createURI(EX + "Error");
    }

}
