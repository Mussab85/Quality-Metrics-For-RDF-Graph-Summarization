package www.etis.fr.schemaetraction;

import java.io.InputStream;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

/**
 * Class to count the total number of distinct triples (statements) in an RDF model.
 * 
 * The method `getClassInstancessNumber` returns the count of distinct subjects in triples.
 * 
 * Note: Method name suggests counting instances of classes but actually counts total triples.
 * 
 * Author: (Your Name)
 */
public class Triplenumber {

    /**
     * Loads an RDF model from a given file path.
     *
     * @param filename Path to the RDF file
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
     * Returns the total count of distinct subjects in the RDF model, 
     * effectively the number of resource instances with any property.
     *
     * @param rdfInputIdealSummary Path to the RDF file
     * @return Number of distinct resource instances (subjects)
     */
    public int getClassInstancessNumber(String rdfInputIdealSummary) {
        Model model = loadModel(rdfInputIdealSummary);

        // SPARQL query to count distinct subjects (?t) across all triples
        String sparqlQueryString = 
            "PREFIX rdf:<" + RDF.getURI() + "> " +
            "PREFIX foaf:<" + FOAF.getURI() + "> " +
            "SELECT (COUNT(DISTINCT ?t) AS ?i) WHERE { ?t ?y ?v . }";

        int instanceCount = 0;

        Query query = QueryFactory.create(sparqlQueryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            if (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                String countStr = sol.get("i").toString();
                // Strip datatype suffix if present (e.g., "12345^^xsd:integer")
                instanceCount = Integer.parseInt(countStr.split("\\^")[0]);
                System.out.println("Total distinct instances (subjects): " + instanceCount);
            }
        }

        return instanceCount;
    }
}
