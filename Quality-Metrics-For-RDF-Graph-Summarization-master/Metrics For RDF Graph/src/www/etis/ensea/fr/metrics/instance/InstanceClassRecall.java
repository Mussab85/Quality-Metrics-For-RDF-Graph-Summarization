package www.etis.ensea.fr.metrics.instance;

import java.util.ArrayList;

import www.etis.ensea.fr.metrics.common.Pattern;
import www.etis.fr.schemaetraction.Triplenumber;

import org.apache.commons.collections15.CollectionUtils;

/**
 * Class to compute the recall metric for instance classes based on RDF graph summaries.
 * Recall measures the proportion of ideal summary instances covered by the produced patterns.
 * 
 * @author Mussab Zneika
 */
public class InstanceClassRecall {

    /**
     * Computes the recall of instance classes by comparing the subjects covered by the given patterns
     * against the total number of instances in the ideal summary RDF file.
     * 
     * @param pas List of Patterns representing the produced summary.
     * @param idealSummaryRDFFile Path to the RDF file representing the ideal summary.
     * @return recall value as a double between 0 and 1.
     */
    public double getInstanceClassRecall(ArrayList<Pattern> pas, String idealSummaryRDFFile) {
        // Aggregate all subjects covered by the patterns
        ArrayList<String> allSubjects = new ArrayList<>();
        for (Pattern pattern : pas) {
            allSubjects = (ArrayList<String>) CollectionUtils.union(allSubjects, pattern.getSubjects());
        }

        // Use Triplenumber to retrieve the total number of instances in the ideal summary
        Triplenumber tp = new Triplenumber();
        double patternsClassInstancesNumber = allSubjects.size();
        double idealSummaryClassInstancesNumber = tp.getClassInstancessNumber(idealSummaryRDFFile);

        // Calculate recall as ratio of instances covered by patterns to total instances in ideal summary
        if (idealSummaryClassInstancesNumber == 0) {
            // Avoid division by zero; return 0 recall if no instances in ideal summary
            return 0;
        }
        double instanceClassRecallValue = patternsClassInstancesNumber / idealSummaryClassInstancesNumber;
        return instanceClassRecallValue;
    }
}
