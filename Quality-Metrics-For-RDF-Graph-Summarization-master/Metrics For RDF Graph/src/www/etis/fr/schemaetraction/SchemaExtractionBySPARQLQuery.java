package www.etis.fr.schemaetraction;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import www.etis.ensea.fr.metrics.common.RDFClass;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

/**
 * Extracts RDF schema information by running SPARQL queries on an RDF dataset.
 * 
 * Specifically, extracts all distinct classes used in the model and their associated properties.
 * For each class, it counts the number of instances and lists all properties used by those instances.
 * 
 * Returns a list of RDFClass objects with type, properties, and instance count.
 * 
 * Author: Mussab Zneika
 */
public class SchemaExtractionBySPARQLQuery {

    /**
     * Loads an RDF model from a file.
     * 
     * @param filename path to the RDF file
     * @return Jena Model loaded from the file
     */
    public static Model loadModel(String filename) {
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(filename);
        if (in == null) {
            throw new IllegalArgumentException("File: " + filename + " not found");
        }
        model.read(in, null);
        return model;
    }

    /**
     * Extracts schema information from an RDF file by querying for classes and their properties.
     * 
     * @param idealSummaryPathName path to the RDF file
     * @return list of RDFClass objects with type, properties, and instance count
     * @throws IOException in case of file read errors
     */
    public ArrayList<RDFClass> extractSchemaInformation(String idealSummaryPathName) throws IOException {
        Model model = loadModel(idealSummaryPathName);

        // SPARQL query to get distinct rdf:types (classes) and their instance counts
        String queryString =
            "PREFIX rdf:<" + RDF.getURI() + "> " +
            "PREFIX foaf:<" + FOAF.getURI() + "> " +
            "SELECT DISTINCT ?class (COUNT(DISTINCT ?instance) AS ?count) WHERE { " +
            "  ?instance rdf:type ?class . " +
            "} GROUP BY ?class";

        Query query = QueryFactory.create(queryString);
        ArrayList<RDFClass> classes = new ArrayList<>();

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String classURI = solution.get("class").toString();

                RDFClass rdfClass = new RDFClass();
                rdfClass.setType(classURI);

                // Get instance count as integer (strip datatype suffix)
                String countStr = solution.get("count").toString();
                int instanceCount = Integer.parseInt(countStr.split("\\^")[0]);
                rdfClass.setNbinstances(instanceCount);

                // Query for properties used by instances of this class
                String propertyQueryString =
                    "PREFIX rdf:<" + RDF.getURI() + "> " +
                    "PREFIX foaf:<" + FOAF.getURI() + "> " +
                    "SELECT ?property (COUNT(?property) AS ?propCount) WHERE { " +
                    "  ?instance rdf:type <" + classURI + "> ; " +
                    "            ?property ?value . " +
                    "} GROUP BY ?property";

                Query propertyQuery = QueryFactory.create(propertyQueryString);

                ArrayList<String> properties = new ArrayList<>();
                // Add rdf:type as a default property
                properties.add(RDF.type.getURI());

                try (QueryExecution propExec = QueryExecutionFactory.create(propertyQuery, model)) {
                    ResultSet propResults = propExec.execSelect();

                    while (propResults.hasNext()) {
                        QuerySolution propSolution = propResults.nextSolution();
                        String propertyURI = propSolution.get("property").toString();
                        properties.add(propertyURI);
                    }
                }

                rdfClass.setProperties(properties);
                classes.add(rdfClass);
            }
        }

        return classes;
    }
}
