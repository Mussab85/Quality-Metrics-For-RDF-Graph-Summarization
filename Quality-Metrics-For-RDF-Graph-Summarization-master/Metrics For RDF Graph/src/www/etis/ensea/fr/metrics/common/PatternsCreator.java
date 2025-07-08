package www.etis.ensea.fr.metrics.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * PatternsCreator is responsible for parsing pattern files and predicate mapping files
 * to create Pattern objects representing RDF summary patterns.
 * Author: Mussab Zneika
 * Version: 1.0
 */
public class PatternsCreator {
    
    // HashMap storing predicate id (Integer) and corresponding URI or label (String)
    private HashMap<Integer, String> allPredicates = new HashMap<>();

    /**
     * Loads the predicate hash table from a file where each line is formatted as:
     * <predicate_id> <predicate_URI>
     * 
     * @param predicate_file path to the predicate mapping file
     * @throws IOException if the file cannot be read
     */
    public void createPredicatesHashTable(String predicate_file) throws IOException {
        try (BufferedReader inp = new BufferedReader(new FileReader(predicate_file))) {
            while (inp.ready()) {
                String res = inp.readLine();
                String[] parts = res.split(" ");
                allPredicates.put(Integer.parseInt(parts[0]), parts[1]);
            }
        }
    }

    /**
     * Writes the subjects string to a specified file.
     * 
     * @param filename file to write the subject string to
     * @param context string content representing subjects
     * @throws IOException if file writing fails
     */
    public void writePatternSubjects(String filename, String context) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(context);
            bw.flush();
        }
    }

    /**
     * Creates patterns from a pattern file and a predicate mapping file.
     * Each line in the pattern file is expected to be in the format like:
     * "1 4 6(5000) 9 8 22 12 45 788 96"
     * where numbers before parentheses are predicate IDs, and number inside parentheses is pattern size.
     * 
     * This method maps predicates, classes, and writes subject references to files.
     * 
     * @param pattern_file path to the pattern file
     * @param predicate_file path to the predicate mapping file
     * @return list of Pattern objects parsed from files
     * @throws IOException if files cannot be read or written
     */
    public ArrayList<Pattern> createPatterns(String pattern_file, String predicate_file) throws IOException {
        createPredicatesHashTable(predicate_file);
        ArrayList<Pattern> patterns = new ArrayList<>();
        try (BufferedReader inp1 = new BufferedReader(new FileReader(pattern_file))) {
            int i = 1;
            while (inp1.ready()) {
                String pattern = inp1.readLine();
                if (pattern != null && !pattern.isEmpty()) {
                    // Split around '(' to separate predicates and size + subjects
                    String[] parts = pattern.split("\\(");
                    parts[0] = parts[0].trim();
                    String[] sizeAndSubjects = parts[1].split("\\)");
                    String[] predicateIds = parts[0].split(" ");

                    ArrayList<String> pattern_properties = new ArrayList<>();
                    ArrayList<String> pattern_classes = new ArrayList<>();

                    // Parse predicates and classify as class or property
                    for (String predIdStr : predicateIds) {
                        if (!predIdStr.isEmpty()) {
                            String predicate = allPredicates.get(Integer.parseInt(predIdStr));
                            if (predicate.contains("::C")) {
                                pattern_classes.add(predicate);
                            } else {
                                pattern_properties.add(predicate);
                            }
                        }
                    }
                    // Create Pattern object
                    Pattern pat = new Pattern(i, pattern_properties, pattern_classes,
                            Integer.parseInt(sizeAndSubjects[0]), "patterns/Pattern" + i);

                    // Write subject references to a file
                    String subjectsString = sizeAndSubjects[1].trim();
                    writePatternSubjects("patterns/Pattern" + i, subjectsString.substring(2, subjectsString.length() - 1));

                    patterns.add(pat);
                    i++;
                }
            }
        }
        return patterns;
    }

    /**
     * An alternative pattern creation method that also extracts subject lists for each pattern.
     * This method differs by setting the subjects property of each Pattern.
     * 
     * @param pattern_file path to the pattern file
     * @param predicate_file path to the predicate mapping file
     * @return list of Pattern objects with subjects included
     * @throws IOException if files cannot be read
     */
    public ArrayList<Pattern> createPatterns1(String pattern_file, String predicate_file) throws IOException {
        createPredicatesHashTable(predicate_file);
        ArrayList<Pattern> patterns = new ArrayList<>();

        try (BufferedReader inp1 = new BufferedReader(new FileReader(pattern_file))) {
            int i = 1;
            while (inp1.ready()) {
                String pattern = inp1.readLine();
                if (pattern != null && !pattern.isEmpty()) {
                    String[] parts = pattern.split("\\(");
                    parts[0] = parts[0].trim();
                    String[] sizeAndSubjects = parts[1].split("\\)");

                    // Extract subjects as a list of strings
                    ArrayList<String> subjects = new ArrayList<>(Arrays.asList(sizeAndSubjects[1].split(" ")));
                    String[] predicateIds = parts[0].split(" ");

                    ArrayList<String> pattern_properties = new ArrayList<>();
                    ArrayList<String> pattern_classes = new ArrayList<>();

                    for (String predIdStr : predicateIds) {
                        if (!predIdStr.isEmpty()) {
                            String predicate = allPredicates.get(Integer.parseInt(predIdStr));
                            if (predicate.contains("::C")) {
                                // Remove the "::C" suffix for class predicates
                                pattern_classes.add(predicate.split("::C")[0]);
                            } else if (!predicate.contains("::R")) {
                                // Exclude relations marked "::R"
                                pattern_properties.add(predicate);
                            }
                        }
                    }

                    // Create Pattern object and set subjects
                    Pattern pat = new Pattern(i, pattern_properties, pattern_classes, Integer.parseInt(sizeAndSubjects[0]), pattern);
                    pat.setSubjects(subjects);

                    patterns.add(pat);
                    i++;
                }
            }
        }
        return patterns;
    }

    /**
     * Filters patterns that have no classes.
     * 
     * @param pats list of patterns to filter
     * @return list of patterns with empty classes list
     */
    public ArrayList<Pattern> getPatternsWithNoClass(ArrayList<Pattern> pats) {
        ArrayList<Pattern> noClassPatterns = new ArrayList<>();
        for (Pattern pattern : pats) {
            if (pattern.getClasses().isEmpty()) {
                noClassPatterns.add(pattern);
            }
        }
        return noClassPatterns;
    }

    /**
     * Filters patterns that have one or more classes.
     * 
     * @param pats list of patterns to filter
     * @return list of patterns with non-empty classes list
     */
    public ArrayList<Pattern> getPatternsWithClasses(ArrayList<Pattern> pats) {
        ArrayList<Pattern> classPatterns = new ArrayList<>();
        for (Pattern pattern : pats) {
            if (!pattern.getClasses().isEmpty()) {
                classPatterns.add(pattern);
            }
        }
        return classPatterns;
    }
}
