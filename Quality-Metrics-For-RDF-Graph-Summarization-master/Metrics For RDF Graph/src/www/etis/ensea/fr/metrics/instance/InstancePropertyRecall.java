package www.etis.ensea.fr.metrics.instance;

import java.util.ArrayList;

import org.apache.commons.collections15.CollectionUtils;

import www.etis.ensea.fr.metrics.common.*;

/**
 * Class to compute recall metric for instance properties in RDF graph summarization.
 * Recall here measures the proportion of relevant property instances retrieved by the summary.
 * 
 * @author Mussab Zneika
 */
public class InstancePropertyRecall {

    /**
     * Returns the subjects covered by a pattern if it contains the given property.
     * Otherwise, returns an empty list.
     * 
     * @param property The property URI to check.
     * @param pattern The Pattern object to inspect.
     * @return List of subject strings if property is present, else empty list.
     */
    private ArrayList<String> cover(String property, Pattern pattern) {
        if (pattern.getProperites().contains(property)) {
            return pattern.getSubjects();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Gets the union of all retrieved subject instances for a given property across all patterns.
     * 
     * @param property The property URI.
     * @param patterns List of Pattern objects.
     * @return Union list of subject instances for the property.
     */
    private ArrayList<String> getListOfAllRetrievedPropertyInstances(String property, ArrayList<Pattern> patterns) {
        ArrayList<String> allRetrievedInstances = new ArrayList<>();
        for (Pattern pattern : patterns) {
            allRetrievedInstances = (ArrayList<String>) CollectionUtils.union(allRetrievedInstances, cover(property, pattern));
        }
        return allRetrievedInstances;
    }

    /**
     * Calculates recall for a single property.
     * Recall = (number of retrieved instances) / (total expected instances)
     * Caps recall at 1 to handle overlapping instances.
     * 
     * @param property The property URI.
     * @param totalPropertyInstances Total number of instances expected for this property.
     * @param patterns List of Pattern objects representing the summary.
     * @return Recall value between 0 and 1.
     */
    private double getPropertyRecall(String property, double totalPropertyInstances, ArrayList<Pattern> patterns) {
        ArrayList<String> retrievedInstances = getListOfAllRetrievedPropertyInstances(property, patterns);
        double retrievedCount = retrievedInstances.size();
        double recall = retrievedCount / totalPropertyInstances;
        return recall > 1 ? 1 : recall;
    }

    /**
     * Computes the weighted recall over all properties.
     * Ignores the RDF Schema label property as it is not relevant.
     * 
     * @param patterns List of Pattern objects representing the produced summary.
     * @param properties List of properties with instance counts in the format "count;propertyURI".
     * @return Weighted recall score between 0 and 1.
     */
    public double getInstancePropertyRecall(ArrayList<Pattern> patterns, ArrayList<String> properties) {
        double totalRecall = 0;
        double totalInstanceCount = 0;

        // Calculate total number of property instances (excluding rdf-schema label)
        for (String pr : properties) {
            String[] parts = pr.split(";");
            if (parts.length == 2 && !parts[1].equals("http://www.w3.org/2000/01/rdf-schema#label")) {
                double propertyInstanceCount = Double.parseDouble(parts[0]);
                totalInstanceCount += propertyInstanceCount;
            }
        }

        // Calculate weighted recall for each property
        for (String pr : properties) {
            String[] parts = pr.split(";");
            if (parts.length == 2 && !parts[1].equals("http://www.w3.org/2000/01/rdf-schema#label")) {
                double propertyInstanceCount = Double.parseDouble(parts[0]);
                double weight = propertyInstanceCount / totalInstanceCount;
                double propertyRecall = getPropertyRecall(parts[1], propertyInstanceCount, patterns);
                totalRecall += weight * propertyRecall;
            }
        }

        return totalRecall;
    }
}
