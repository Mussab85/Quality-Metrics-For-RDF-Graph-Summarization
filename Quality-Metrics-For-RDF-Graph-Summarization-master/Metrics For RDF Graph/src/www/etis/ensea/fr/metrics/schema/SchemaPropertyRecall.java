package www.etis.ensea.fr.metrics.schema;

import java.util.ArrayList;

import org.apache.commons.collections15.CollectionUtils;

import www.etis.ensea.fr.metrics.common.Pattern;
import www.etis.ensea.fr.metrics.common.RDFClass;

/**
 * Computes recall metric for schema properties.
 * Measures how many of the schema class properties are covered by the properties in the patterns.
 * 
 * Recall = (Number of shared properties between classes and patterns) / (Total properties in schema classes)
 * 
 * Author: Mussab Zneika
 */
public class SchemaPropertyRecall {

    /**
     * Calculates the schema property recall.
     * 
     * @param patterns List of Patterns extracted from the summary.
     * @param classes List of RDFClasses representing the schema.
     * @return recall value for schema properties.
     */
    public double getSchemaPropertyRecall(ArrayList<Pattern> patterns, ArrayList<RDFClass> classes) {
        // Collect all properties present in the patterns
        ArrayList<String> allPatternProperties = new ArrayList<>();
        for (Pattern pattern : patterns) {
            allPatternProperties = (ArrayList<String>) CollectionUtils.union(allPatternProperties, pattern.getProperites());
        }

        // Remove irrelevant properties from schema classes
        for (RDFClass rdfClass : classes) {
            rdfClass.getProperties().remove("http://www.w3.org/2000/01/rdf-schema#comment");
            rdfClass.getProperties().remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        }

        // Collect all properties present in the schema classes
        ArrayList<String> allClassProperties = new ArrayList<>();
        for (RDFClass rdfClass : classes) {
            allClassProperties = (ArrayList<String>) CollectionUtils.union(allClassProperties, rdfClass.getProperties());
        }

        // Intersection: properties common to both schema classes and patterns
        ArrayList<String> sharedProperties = (ArrayList<String>) CollectionUtils.intersection(allClassProperties, allPatternProperties);

        double totalClassProperties = allClassProperties.size();
        double totalSharedProperties = sharedProperties.size();

        // Avoid division by zero if no properties in classes
        if (totalClassProperties == 0) {
            return 0;
        }

        // Recall = fraction of class properties that appear in patterns
        return totalSharedProperties / totalClassProperties;
    }
}
