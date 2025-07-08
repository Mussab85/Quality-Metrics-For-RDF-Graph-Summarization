package www.etis.ensea.fr.metrics.common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * RDFClass represents an RDF class with its associated properties and number of instances.
 * 
 * Author: Mussab Zneika
 * Version: 1.0
 */
public class RDFClass implements Serializable {
    // URI or name representing the RDF class type
    private String type;
    
    // List of properties associated with this RDF class
    private ArrayList<String> properties;
    
    // Number of instances of this RDF class
    private int nbinstances;

    /**
     * Default constructor.
     */
    public RDFClass() {
        // No-argument constructor
    }

    /**
     * Constructor initializing the type and properties of the RDF class.
     * 
     * @param type The RDF class type URI or label
     * @param properties List of properties related to this RDF class
     */
    public RDFClass(String type, ArrayList<String> properties) {
        this.type = type;
        this.properties = properties;
    }

    /**
     * Gets the RDF class type.
     * 
     * @return type of the RDF class
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the RDF class type.
     * 
     * @param type RDF class type URI or label
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the list of properties associated with the RDF class.
     * 
     * @return list of property URIs or labels
     */
    public ArrayList<String> getProperties() {
        return properties;
    }

    /**
     * Sets the list of properties for the RDF class.
     * 
     * @param properties list of property URIs or labels
     */
    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    /**
     * Gets the number of instances of this RDF class.
     * 
     * @return number of instances
     */
    public int getNbinstances() {
        return nbinstances;
    }

    /**
     * Sets the number of instances of this RDF class.
     * 
     * @param nbinstances number of instances
     */
    public void setNbinstances(int nbinstances) {
        this.nbinstances = nbinstances;
    }
}
