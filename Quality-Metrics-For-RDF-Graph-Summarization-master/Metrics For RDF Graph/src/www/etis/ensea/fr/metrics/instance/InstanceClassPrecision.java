package www.etis.ensea.fr.metrics.instance;

import java.util.ArrayList;
import org.apache.commons.collections15.CollectionUtils;
import www.etis.ensea.fr.metrics.common.*;

/**
 * This class computes the precision metric for instance classes
 * based on coverage of RDF patterns.
 * It evaluates how well the patterns cover instances of RDF classes.
 * 
 * @author Mussab Zneika
 */
public class InstanceClassPrecision {

    /**
     * Tests if the pattern contains the same RDF class type.
     * Returns 1 if the pattern contains the class type; otherwise 0.
     * 
     * @param p the pattern to test
     * @param c the RDF class to compare
     * @return integer 0 or 1 indicating if types match
     */
    private int testSameType(Pattern p, RDFClass c) {
        return p.getClasses().contains(c.getType()) ? 1 : 0;
    }

    /**
     * Returns the subjects covered by a pattern if it matches the RDF class type.
     * Otherwise, returns an empty list.
     * 
     * @param pa the pattern
     * @param c the RDF class
     * @return list of subjects covered by the pattern
     */
    private ArrayList<String> cover(Pattern pa, RDFClass c) {
        ArrayList<String> result = new ArrayList<>();
        if (testSameType(pa, c) >= 1)
            return pa.getSubjects();
        else
            return result;
    }

    /**
     * Aggregates subjects covered by all patterns matching the RDF class.
     * 
     * @param pas list of patterns
     * @param c the RDF class
     * @return aggregated list of subjects covered by patterns for the class
     */
    private ArrayList<String> coverAll(ArrayList<Pattern> pas, RDFClass c) {
        ArrayList<String> result = new ArrayList<>();
        for (Pattern pa : pas) {
            ArrayList<String> partial = cover(pa, c);
            if (!partial.isEmpty()) {
                result = (ArrayList<String>) CollectionUtils.union(result, partial);
            }
        }
        return result;
    }

    /**
     * Calculates precision for a given RDF class based on coverage by patterns.
     * Precision = number of instances covered / total instances of the class.
     * The value is capped at 1.
     * 
     * @param c the RDF class
     * @param pas list of patterns
     * @return precision value between 0 and 1
     */
    private double precision(RDFClass c, ArrayList<Pattern> pas) {
        double coveredInstances = coverAll(pas, c).size();
        double totalInstances = c.getNbinstances();
        double prec = coveredInstances / totalInstances;
        return Math.min(prec, 1.0);
    }

    /**
     * Computes the weighted instance class precision over all RDF classes.
     * Weights are based on class instance counts relative to total instances.
     * 
     * @param pas list of patterns
     * @param classes list of RDF classes
     * @param totalInstanceNumber total number of instances (unused, kept for compatibility)
     * @return weighted precision value
     */
    public double getInstanceClassPrecision(ArrayList<Pattern> pas, ArrayList<RDFClass> classes, int totalInstanceNumber) {
        double precisionSum = 0;
        int totalInstancesCount = 0;

        for (RDFClass c : classes) {
            totalInstancesCount += c.getNbinstances();
        }

        for (RDFClass c : classes) {
            double weight = (double) c.getNbinstances() / totalInstancesCount;
            precisionSum += weight * precision(c, pas);
        }

        return precisionSum;
    }
}
