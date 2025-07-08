package www.etis.ensea.fr.metrics.schema;

import java.util.ArrayList;
import org.apache.commons.collections15.CollectionUtils;
import www.etis.ensea.fr.metrics.common.*;

/**
 * Computes the recall metric for schema classes in RDF graph summarization.
 * Measures how well the properties of RDF classes are covered by the patterns.
 * 
 * @author Mussab Zneika
 */
public class SchemaClassRecall {

    /**
     * Calculates the schema class recall over all classes.
     * Recall = (Number of class properties covered by patterns) / (Total properties of class)
     * 
     * @param patterns List of Patterns extracted from the summary.
     * @param classes List of RDFClasses to compare against.
     * @return average recall value over all classes.
     */
    public double getSchemaClassRecall(ArrayList<Pattern> patterns, ArrayList<RDFClass> classes) {
        ArrayList<String> allPropertiesFromPatterns = new ArrayList<>();

        // Aggregate properties from all patterns, adding rdf:type if pattern has classes
        for (Pattern pattern : patterns) {
            if (pattern.getClasses() != null && !pattern.getClasses().isEmpty()) {
                pattern.getProperites().add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
            }
            allPropertiesFromPatterns = (ArrayList<String>) CollectionUtils.union(allPropertiesFromPatterns, pattern.getProperites());
        }

        double totalRecall = 0;

        for (RDFClass rdfClass : classes) {
            // Intersection of pattern properties with class properties
            ArrayList<String> intersection = (ArrayList<String>) CollectionUtils.intersection(allPropertiesFromPatterns, rdfClass.getProperties());

            // Recall for this class: fraction of class properties covered by patterns
            double classRecall = (double) intersection.size() / rdfClass.getProperties().size();
            totalRecall += classRecall;
        }

        // Average recall over all classes
        return classes.isEmpty() ? 0 : totalRecall / classes.size();
    }
}
