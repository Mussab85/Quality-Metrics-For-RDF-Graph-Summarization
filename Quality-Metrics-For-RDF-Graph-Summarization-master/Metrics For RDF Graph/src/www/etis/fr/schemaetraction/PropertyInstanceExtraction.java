package www.etis.fr.schemaetraction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

/**
 * Extracts property instance counts from an RDF model.
 * For each class type in the RDF model, counts occurrences of properties associated with instances of that class.
 * 
 * Output is a list of strings formatted as "count;propertyURI".
 * 
 * Author: Mussab Zneika
 */
public class PropertyInstanceExtraction {
    
    /**
     * Loads an RDF model from a given filename.
     * 
     * @param filename RDF file path
     * @return loaded Jena Model
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
     * Extracts property instance counts from the RDF model located at idealSummaryPathName.
     * 
     * @param idealSummaryPathName RDF file path
     * @return list of strings "count;propertyURI"
     */
    public ArrayList<String> extract(String idealSummaryPathName) {
        Model model = loadModel(idealSummaryPathName);
        Hashtable<String, Double> propertyCounts = new Hashtable<>();
        ArrayList<String> allProperties = new ArrayList<>();

        // SPARQL query to get all distinct rdf:types and their counts
        String sparqlQueryString = 
            "PREFIX rdf:<" + RDF.getURI() + "> " +
            "PREFIX foaf:<" + FOAF.getURI() + "> " +
            "SELECT DISTINCT ?type (COUNT(?instance) AS ?count) WHERE { " +
            "  ?instance rdf:type ?type . " +
            "} GROUP BY ?type";

        Query query = QueryFactory.create(sparqlQueryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String typeURI = solution.get("type").toString();

                // For each type, query properties and their counts for instances of that type
                String propertyCountQueryString = 
                    "PREFIX rdf:<" + RDF.getURI() + "> " +
                    "PREFIX foaf:<" + FOAF.getURI() + "> " +
                    "SELECT ?property (COUNT(?property) AS ?propCount) WHERE { " +
                    "  ?instance rdf:type <" + typeURI + "> ; " +
                    "            ?property ?value . " +
                    "} GROUP BY ?property";

                Query propertyQuery = QueryFactory.create(propertyCountQueryString);

                try (QueryExecution propQexec = QueryExecutionFactory.create(propertyQuery, model)) {
                    ResultSet propResults = propQexec.execSelect();

                    while (propResults.hasNext()) {
                        QuerySolution propSolution = propResults.nextSolution();
                        String propertyURI = propSolution.get("property").toString();

                        // Extract count as double (parsing and cleaning datatype if present)
                        String countStr = propSolution.get("propCount").toString();
                        double count = parseDoubleSafe(countStr);

                        // Aggregate counts for properties
                        propertyCounts.put(propertyURI, propertyCounts.getOrDefault(propertyURI, 0.0) + count);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("PropertyInstanceExtraction Exception: " + e.getMessage());
        }

        // Format output as "count;propertyURI"
        for (String property : propertyCounts.keySet()) {
            allProperties.add(propertyCounts.get(property) + ";" + property);
        }

        return allProperties;
    }

    /**
     * Parses a double from a string, safely removing any datatype suffixes (e.g., "^^xsd:integer").
     * 
     * @param s input string possibly with datatype suffix
     * @return parsed double, or 0 if parsing fails
     */
    private double parseDoubleSafe(String s) {
        if (s == null) return 0;
        try {
            // Remove datatype suffix if present (e.g., "12345^^xsd:integer")
            if (s.contains("^^")) {
                s = s.split("\\^\\^")[0];
            }
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
