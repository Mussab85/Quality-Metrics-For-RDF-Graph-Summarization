package www.etis.ensea.fr.metrics.schema;

import java.util.ArrayList;
import org.apache.commons.collections15.CollectionUtils;
import www.etis.ensea.fr.metrics.common.Pattern;
import www.etis.ensea.fr.metrics.common.RDFClass;

/**
 * Computes precision metric for schema properties.
 * Measures how many of the properties found in the patterns actually belong to the schema classes.
 * 
 * Precision = (Number of shared properties between classes and patterns) / (Total properties in patterns)
 * 
 * Author: Mussab Zneika
 */
public class SchemaPropertyPrecision {

    /**
     * Calculates the schema property precision.
     * 
     * @param patterns List of Patterns extracted from the summary.
     * @param classes List of RDFClasses representing the schema.
     * @return precision value for schema properties.
     */
    public double getSchemaPropertyPrecision(ArrayList<Pattern> patterns, ArrayList<RDFClass> classes) {
        // Collect all properties present in the patterns
        ArrayList<String> allPatternProperties = new ArrayList<>();
        for (Pattern pattern : patterns) {
            allPatternProperties = (ArrayList<String>) CollectionUtils.union(allPatternProperties, pattern.getProperites());
        }

        // Collect all properties present in the schema classes
        ArrayList<String> allClassProperties = new ArrayList<>();
        for (RDFClass rdfClass : classes) {
            allClassProperties = (ArrayList<String>) CollectionUtils.union(allClassProperties, rdfClass.getProperties());
        }

        // Intersection of properties in patterns and schema classes
        ArrayList<String> sharedProperties = (ArrayList<String>) CollectionUtils.intersection(allClassProperties, allPatternProperties);

        double totalPatternProperties = allPatternProperties.size();
        double totalSharedProperties = sharedProperties.size();

        // Avoid division by zero if no properties in patterns
        if (totalPatternProperties == 0) {
            return 0;
        }

        // Precision = fraction of pattern properties that belong to schema classes
        return totalSharedProperties / totalPatternProperties;
    }
}
