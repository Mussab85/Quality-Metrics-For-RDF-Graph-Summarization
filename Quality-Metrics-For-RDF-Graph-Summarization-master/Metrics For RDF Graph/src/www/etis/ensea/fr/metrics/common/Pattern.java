package www.etis.ensea.fr.metrics.common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a pattern in RDF summarization, which captures a set of properties and classes
 * related to a group of instances (subjects). This class is used as a data model to evaluate
 * and compare generated RDF summaries with an ideal summary.
 *
 * <p><b>Software Engineering Roles:</b></p>
 * <ul>
 *   <li><b>Encapsulation:</b> Private fields with public getters/setters ensure safe data access.</li>
 *   <li><b>Reusability:</b> Serializable class enables it to be reused across serialization-based systems.</li>
 *   <li><b>Readability:</b> Clear naming and documentation help future developers understand intent.</li>
 * </ul>
 * 
 * Author: Mussab Zneika  
 * Version: 1.0
 */
public class Pattern implements Serializable {

    // Unique identifier for the pattern
    private int id;

    // List of property URIs in the pattern
    private ArrayList<String> properites;

    // List of class URIs associated with the pattern
    private ArrayList<String> classes;

    // Number of subjects represented by this pattern
    private int size;

    // String reference for subjects (e.g., instance IDs as string)
    private String subjects_ref;

    // List of subject instance identifiers
    private ArrayList<String> subjects;

    // Default constructor
    public Pattern() {
        super();
    }

    /**
     * Full constructor to initialize the pattern with required metadata.
     *
     * @param id           Unique pattern identifier
     * @param properites   List of properties in the pattern
     * @param classes      List of classes associated with the pattern
     * @param size         Number of instances represented by the pattern
     * @param subjects_ref Reference string for subjects (e.g., subject IDs or labels)
     */
    public Pattern(int id, ArrayList<String> properites, ArrayList<String> classes, int size, String subjects_ref) {
        this.id = id;
        this.properites = properites;
        this.classes = classes;
        this.size = size;
        this.subjects_ref = subjects_ref;
    }

    // Getter and Setter methods for all fields below

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getProperites() {
        return properites;
    }

    public void setProperites(ArrayList<String> properites) {
        this.properites = properites;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSubjects_ref() {
        return subjects_ref;
    }

    public void setSubjects_ref(String subjects_ref) {
        this.subjects_ref = subjects_ref;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }
}
