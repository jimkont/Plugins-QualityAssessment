package eu.unifiedviews.plugins.quality.cu1;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;

public class QualityOntology {

    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String DC = "http://purl.org/dc/elements/1.1#";
    public static final String QB = "http://purl.org/linked-data/cube#";
    public static final String DAQ = "http://purl.org/eis/vocab/daq#";
    public static final String EX = "http://comsode.eu/quality/";

    public static URI RDF_A_PREDICATE;

    public static URI DC_DATE;

    public static URI QB_OBSERVATION;
    public static URI QB_STRUCTURE;

    public static URI DAQ_QUALITY_GRAPH;
    public static URI DAQ_DSD;
    public static URI DAQ_DIMENSION;
    public static URI DAQ_HAS_METRIC;
    public static URI DAQ_HAS_OBSERVATION;
    public static URI DAQ_METRIC;
    public static URI DAQ_COMPUTED_ON;
    public static URI DAQ_VALUE;

    public static URI EX_DPU_NAME;
    public static URI EX_TIMELINESS_DIMENSION;
    public static URI[] EX_OBSERVATIONS;

    public static void init(ValueFactory valueFactory, String nameDPU, int observationsNumber) {

        RDF_A_PREDICATE = valueFactory.createURI(RDF +"type");

        DC_DATE = valueFactory.createURI(DC +"date");

        QB_OBSERVATION = valueFactory.createURI(QB +"Observation");
        QB_STRUCTURE = valueFactory.createURI(QB +"structure");

        DAQ_QUALITY_GRAPH = valueFactory.createURI(DAQ + "QualityGraph");
        DAQ_DSD = valueFactory.createURI(DAQ +"dsd");
        DAQ_DIMENSION = valueFactory.createURI(DAQ +"Dimension");
        DAQ_HAS_METRIC = valueFactory.createURI(DAQ +"hasMetric");
        DAQ_HAS_OBSERVATION = valueFactory.createURI(DAQ +"hasObservation");
        DAQ_METRIC = valueFactory.createURI(DAQ +"Metric");
        DAQ_COMPUTED_ON = valueFactory.createURI(DAQ +"computedOn");
        DAQ_VALUE = valueFactory.createURI(DAQ +"value");

        EX_DPU_NAME = valueFactory.createURI(EX + nameDPU);
        EX_TIMELINESS_DIMENSION = valueFactory.createURI(EX +"timelinessDimension");
        EX_OBSERVATIONS = new URI[observationsNumber + 1];

        for (int i = 0; i <= observationsNumber; i++) {
            EX_OBSERVATIONS[i] = valueFactory.createURI(EX +"obs"+ i);
        }
    }
}