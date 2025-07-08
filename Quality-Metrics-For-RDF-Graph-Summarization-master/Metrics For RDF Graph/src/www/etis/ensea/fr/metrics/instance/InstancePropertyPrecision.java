package www.etis.ensea.fr.metrics.instance;

import java.util.ArrayList;
import www.etis.ensea.fr.metrics.common.*;

/**
 * Class to compute the precision metric for instance properties in RDF graph summarization.
 * Precision here measures how well the produced summary covers property instances compared to expected counts.
 * 
 * @author Mussab Zneika
 */
public class InstancePropertyPrecision {

    /**
     * Returns the size of the pattern if it contains the given property, otherwise 0.
     * This represents how many instances the pattern covers for the property.
     * 
     * @param property The property URI to check.
     * @param pattern The Pattern object to inspect.
     * @return The number of instances covered by this pattern for the property, or 0 if none.
     */
    private double cover(String property, Pattern pattern) {
        if (pattern.getProperites().contains(property)) {
            return pattern.getSize();
        } else {
            return 0;
        }
    }

    /**
     * Sums the coverage of all patterns for a given property.
     * 
     * @param property The property URI.
     * @param patterns List of Pattern objects representing the summary.
     * @return The total number of instances covered by all patterns for this property.
     */
    private double coverAllPatterns(String property, ArrayList<Pattern> patterns) {
        double totalCoverage = 0;
        for (Pattern pattern : patterns) {
            totalCoverage += cover(property, pattern);
        }
        return totalCoverage;
    }

    /**
     * Computes the precision for a given property.
     * Precision = (expected number of instances) / (number of instances covered by summary)
     * Caps the precision at 1 to handle overlapping counts.
     * 
     * @param property The property URI.
     * @param expectedInstanceCount Expected number of instances for this property.
     * @param patterns List of Pattern objects representing the summary.
     * @return Precision value between 0 and 1.
     */
    private double precision(String property, double expectedInstanceCount, ArrayList<Pattern> patterns) {
        double coveredCount = coverAllPatterns(property, patterns);
        if (coveredCount == 0) {
            return 0; // Avoid division by zero
        }
        double prec = expectedInstanceCount / coveredCount;
        return prec > 1 ? 1 : prec;
    }

    /**
     * Computes the overall instance property precision weighted by the number of instances per property.
     * Ignores the RDF Schema label property as it is not relevant for this metric.
     * 
     * @param patterns List of Pattern objects representing the produced summary.
     * @param properties List of properties with instance counts in the format "count;propertyURI".
     * @return Weighted precision score between 0 and 1.
     */
    public double getInstancePropertyPrecision(ArrayList<Pattern> patterns, ArrayList<String> properties) {
        double totalPrecision = 0;
        double totalInstanceCount = 0;

        // Calculate total number of property instances (excluding rdf-schema label)
        for (String pr : properties) {
            String[] parts = pr.split(";");
            if (parts.length == 2 && !parts[1].equals("http://www.w3.org/2000/01/rdf-schema#label")) {
                double propertyInstanceCount = Double.parseDouble(parts[0]);
                totalInstanceCount += propertyInstanceCount;
            }
        }

        // Calculate weighted precision for each property
        for (String pr : properties) {
            String[] parts = pr.split(";");
            if (parts.length == 2 && !parts[1].equals("http://www.w3.org/2000/01/rdf-schema#label")) {
                double propertyInstanceCount = Double.parseDouble(parts[0]);
                double weight = propertyInstanceCount / totalInstanceCount;
                double propertyPrecision = precision(parts[1], propertyInstanceCount, patterns);
                totalPrecision += weight * propertyPrecision;
            }
        }

        return totalPrecision;
    }
}
