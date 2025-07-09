package www.etis.ensea.fr.main;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import java.util.HashSet;
import java.util.Set;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import www.etis.ensea.fr.metrics.common.Pattern;
import www.etis.ensea.fr.metrics.common.PatternsCreator;
import www.etis.ensea.fr.metrics.common.RDFClass;
import www.etis.ensea.fr.metrics.instance.*;
import www.etis.ensea.fr.metrics.schema.*;
import www.etis.fr.schemaetraction.*;
import www.etis.fr.schemaetraction.SchemaExtractionBySPARQLQuery;

public class EvaluationGUI extends JFrame {

    private JTextField mappedFileField;
    private JTextField propertyMapFileField;
    private JTextField idealSummaryField;
    private JTextArea outputArea;

    public EvaluationGUI() {
        setTitle("📊 RDF Graph Summary Quality Evaluation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        applyLookAndFeel();
        setupFonts();

        JPanel inputPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mappedFileField = new JTextField();
        propertyMapFileField = new JTextField();
        idealSummaryField = new JTextField();

        inputPanel.add(new JLabel("Mapped File:"));
        inputPanel.add(mappedFileField);
        inputPanel.add(createBrowseButton(mappedFileField));

        inputPanel.add(new JLabel("Property Map File:"));
        inputPanel.add(propertyMapFileField);
        inputPanel.add(createBrowseButton(propertyMapFileField));

        inputPanel.add(new JLabel("Ideal Summary (RDF):"));
        inputPanel.add(idealSummaryField);
        inputPanel.add(createBrowseButton(idealSummaryField));

        JButton runButton = new JButton("🚀 Run Evaluation");
        runButton.setBackground(new Color(59, 89, 182));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.addActionListener(this::runEvaluation);

        inputPanel.add(new JLabel()); // empty cell
        inputPanel.add(runButton);

        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBackground(new Color(245, 245, 245));
        outputArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Evaluation Output", TitledBorder.LEFT, TitledBorder.TOP));

        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    private void setupFonts() {
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
    }

    private JButton createBrowseButton(JTextField targetField) {
        JButton button = new JButton("Browse 📁");
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(EvaluationGUI.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                targetField.setText(selectedFile.getAbsolutePath());
            }
        });
        return button;
    }

    private void runEvaluation(ActionEvent e) {
        try {
            String mappedFile = mappedFileField.getText().trim();
            String propertyMapFile = propertyMapFileField.getText().trim();
            String idealSummary = idealSummaryField.getText().trim();

            if (mappedFile.isEmpty() || propertyMapFile.isEmpty() || idealSummary.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all file fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            outputArea.setText("Running evaluation...\n");
            Triplenumber tripleNumber =new Triplenumber();
            PatternsCreator patternCreator = new PatternsCreator();
            SchemaClassRecall schemaCR = new SchemaClassRecall();
            SchemaClassPrecision schemaCP = new SchemaClassPrecision();
            SchemaPropertyRecall schemaPR = new SchemaPropertyRecall();
            SchemaPropertyPrecision schemaPP = new SchemaPropertyPrecision();
            InstanceClassPrecision instanceCP = new InstanceClassPrecision();
            InstanceClassRecall instanceCR = new InstanceClassRecall();
            InstancePropertyPrecision instancePP = new InstancePropertyPrecision();
            InstancePropertyRecall instancePR = new InstancePropertyRecall();

            int totalInstances = tripleNumber.getClassInstancessNumber(idealSummary);
            SchemaExtractionBySPARQLQuery schemaExtractor = new SchemaExtractionBySPARQLQuery();
            ArrayList<Pattern> patterns = patternCreator.createPatterns1(mappedFile, propertyMapFile);
            ArrayList<RDFClass> schemaClasses = schemaExtractor.extractSchemaInformation(idealSummary);
            PropertyInstanceExtraction propertyExtractor = new PropertyInstanceExtraction();
            ArrayList<String> properties = propertyExtractor.extract(idealSummary);
            
            double schemaClassPrecision = schemaCP.getSchemaClassPrecision(patterns, schemaClasses, 3);
            double schemaClassRecall = schemaCR.getSchemaClassRecall(patterns, schemaClasses);
            double schemaPropertyPrecision = schemaPP.getSchemaPropertyPrecision(patterns, schemaClasses);
            double schemaPropertyRecall = schemaPR.getSchemaPropertyRecall(patterns, schemaClasses);

            double instanceClassPrecision = instanceCP.getInstanceClassPrecision(patterns, schemaClasses, totalInstances);
            double instanceClassRecall = instanceCR.getInstanceClassRecall(patterns, idealSummary);
            double instancePropertyPrecision = instancePP.getInstancePropertyPrecision(patterns, properties);
            double instancePropertyRecall = instancePR.getInstancePropertyRecall(patterns, properties);

            outputArea.append("📘 Schema Level Metrics:\n");
            outputArea.append(" - Class Precision: " + schemaClassPrecision + "\n");
            outputArea.append(" - Class Recall: " + schemaClassRecall + "\n");
            outputArea.append(" - Property Precision: " + schemaPropertyPrecision + "\n");
            outputArea.append(" - Property Recall: " + schemaPropertyRecall + "\n\n");

            outputArea.append("📙 Instance Level Metrics:\n");
            outputArea.append(" - Class Precision: " + instanceClassPrecision + "\n");
            outputArea.append(" - Class Recall: " + instanceClassRecall + "\n");
            outputArea.append(" - Property Precision: " + instancePropertyPrecision + "\n");
            outputArea.append(" - Property Recall: " + instancePropertyRecall + "\n");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during evaluation:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int countUniqueSubjects(String rdfFilePath) {
        Set<Resource> subjects = new HashSet<>();
        Model model = ModelFactory.createDefaultModel();
        System.out.println(rdfFilePath);
        String lang = "RDF/XML";
        if (rdfFilePath.endsWith(".ttl")) lang = "TURTLE";
        else if (rdfFilePath.endsWith(".nt")) lang = "N-TRIPLE";
        else if (rdfFilePath.endsWith(".jsonld")) lang = "JSON-LD";

        try {
            model.read(rdfFilePath, null, lang);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load RDF file: " + rdfFilePath + "\n" + ex.getMessage(), ex);
        }

        model.listStatements().forEachRemaining(stmt -> subjects.add(stmt.getSubject()));
        return subjects.size();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EvaluationGUI().setVisible(true));
    }
}
