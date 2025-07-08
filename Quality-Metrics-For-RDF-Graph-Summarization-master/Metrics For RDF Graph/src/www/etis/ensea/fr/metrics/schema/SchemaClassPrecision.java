package www.etis.ensea.fr.metrics.schema;

import java.util.ArrayList;

import org.apache.commons.collections15.CollectionUtils;

import www.etis.ensea.fr.metrics.common.*;

/**
 * Computes precision metric for schema classes in RDF graph summarization.
 * Measures similarity between RDF classes and knowledge patterns.
 * 
 * @author Mussab Zneika
 */
public class SchemaClassPrecision {

    /**
     * Tests if a pattern has no typeOf information.
     * @param p The pattern to test.
     * @return 1 if pattern has no classes or empty classes, 0 otherwise.
     */
    private int typeTest(Pattern p) {
        return (p.getClasses() == null || p.getClasses().isEmpty()) ? 1 : 0;
    }

    /**
     * Tests if a pattern has the same type as the RDFClass.
     * @param p The pattern to test.
     * @param c The RDFClass to compare against.
     * @return 1 if pattern classes contain RDFClass type, 0 otherwise.
     */
    private int testSameType(Pattern p, RDFClass c) {
        return p.getClasses().contains(c.getType()) ? 1 : 0;
    }

    /**
     * Defines the L(c, p) function which evaluates the relevance of a pattern to a class.
     * @param c The RDFClass.
     * @param p The Pattern.
     * @return int value representing the relevance (0, 1 or 2).
     */
    private int L(RDFClass c, Pattern p) {
        if (p.getProperites().isEmpty() && p.getClasses().contains(c.getType())) {
            return 1;
        }
        ArrayList<String> commonProperties = (ArrayList<String>) CollectionUtils.intersection(p.getProperites(), c.getProperties());
        if (commonProperties.size() > 0) {
            int l = testSameType(p, c);
            int t = typeTest(p);
            return t + l;
        }
        return 0;
    }

    /**
     * Computes similarity between a pattern and a class.
     * @param p The Pattern.
     * @param c The RDFClass.
     * @return similarity score as double.
     */
    private double similarity(Pattern p, RDFClass c) {
        if (p.getProperites().isEmpty() && p.getClasses().contains(c.getType())) {
            return 1.0;
        }
        int l = testSameType(p, c);
        int t = typeTest(p);
        int patternSize = p.getProperites().size();

        ArrayList<String> commonProperties = (ArrayList<String>) CollectionUtils.intersection(p.getProperites(), c.getProperties());
        double size = commonProperties.size();

        if (size > 0) {
            return (l + t) * (size / (double) patternSize);
        }
        return 0;
    }

    /**
     * Weight function to adjust similarity based on the number of patterns representing a class.
     * @param c The RDFClass.
     * @param d A parameter controlling the decay.
     * @param patterns List of Patterns.
     * @return weight value as double.
     */
    private double weight(RDFClass c, double d, ArrayList<Pattern> patterns) {
        double x = NumberOfPatternsRepresentClass(c, patterns);
        double z = Math.exp(1 - Math.pow(x, 1 / d));
        double y = 1 - z;
        return 1 - y;
    }

    /**
     * Finds the maximum number of patterns representing any class.
     * @param patterns List of Patterns.
     * @param classes List of RDFClasses.
     * @return max count as double.
     */
    private double getMax(ArrayList<Pattern> patterns, ArrayList<RDFClass> classes) {
        double max = NumberOfPatternsRepresentClass(classes.get(0), patterns);
        for (RDFClass rdfClass : classes) {
            double current = NumberOfPatternsRepresentClass(rdfClass, patterns);
            if (current > max) max = current;
        }
        return max;
    }

    /**
     * Counts how many patterns represent a given class.
     * @param c The RDFClass.
     * @param patterns List of Patterns.
     * @return sum of L(c, p) over all patterns.
     */
    private double NumberOfPatternsRepresentClass(RDFClass c, ArrayList<Pattern> patterns) {
        double sum = 0;
        for (Pattern p : patterns) {
            sum += L(c, p);
        }
        return sum;
    }

    /**
     * Calculates total similarity for a class adjusted by weight.
     * @param c The RDFClass.
     * @param patterns List of Patterns.
     * @param a parameter controlling weighting.
     * @return weighted similarity score.
     */
    private double totalSimilarity(RDFClass c, ArrayList<Pattern> patterns, double a) {
        double sumSim = 0;
        double sumL = 0;
        for (Pattern p : patterns) {
            double sim = similarity(p, c);
            sumSim += sim;
            sumL += L(c, p);
        }
        if (sumL == 0) {
            return 0;
        }
        return weight(c, a, patterns) * (sumSim / sumL);
    }

    /**
     * Computes overall schema class precision across all classes.
     * @param patterns List of Patterns.
     * @param classes List of RDFClasses.
     * @param a weighting parameter.
     * @param max max number of patterns representing any class (not used here but kept for signature).
     * @return precision score as double.
     */
    private double getClassPrecision(ArrayList<Pattern> patterns, ArrayList<RDFClass> classes, double a, double max) {
        double sumPrecision = 0;
        double countedClasses = 0;
        for (RDFClass c : classes) {
            double sim = totalSimilarity(c, patterns, a);
            sumPrecision += sim;
            if (NumberOfPatternsRepresentClass(c, patterns) > 0) {
                countedClasses++;
            }
        }
        return (countedClasses == 0) ? 0 : (sumPrecision / countedClasses);
    }

    /**
     * Main method to calculate schema class precision.
     * Removes irrelevant properties from patterns and classes before computation.
     * 
     * @param patterns List of Patterns.
     * @param classes List of RDFClasses.
     * @param a weighting parameter.
     * @return schema class precision score.
     */
    public double getSchemaClassPrecision(ArrayList<Pattern> patterns, ArrayList<RDFClass> classes, double a) {
        // Remove common irrelevant properties
        for (Pattern p : patterns) {
            p.getProperites().remove("http://www.w3.org/2002/07/owl#sameAs");
            p.getProperites().remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
            p.getProperites().remove("http://www.w3.org/2000/01/rdf-schema#label");
        }
        for (RDFClass c : classes) {
            c.getProperties().remove("http://www.w3.org/2002/07/owl#sameAs");
            c.getProperties().remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
            c.getProperties().remove("http://www.w3.org/2000/01/rdf-schema#label");
        }
        double max = getMax(patterns, classes);
        return getClassPrecision(patterns, classes, a, max);
    }
}
