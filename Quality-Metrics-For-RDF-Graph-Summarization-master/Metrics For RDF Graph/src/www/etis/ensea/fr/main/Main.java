package www.etis.ensea.fr.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import www.etis.ensea.fr.metrics.common.Pattern;
import www.etis.ensea.fr.metrics.common.PatternsCreator;
import www.etis.ensea.fr.metrics.common.RDFClass;
import www.etis.ensea.fr.metrics.instance.InstanceClassPrecision;
import www.etis.ensea.fr.metrics.instance.InstanceClassRecall;
import www.etis.ensea.fr.metrics.instance.InstancePropertyPrecision;
import www.etis.ensea.fr.metrics.instance.InstancePropertyRecall;
import www.etis.ensea.fr.metrics.schema.SchemaClassPrecision;
import www.etis.ensea.fr.metrics.schema.SchemaClassRecall;
import www.etis.ensea.fr.metrics.schema.SchemaPropertyPrecision;
import www.etis.ensea.fr.metrics.schema.SchemaPropertyRecall;
import www.etis.fr.schemaetraction.PropertyInstanceExtraction;
import www.etis.fr.schemaetraction.SchemaExtractionBySPARQLQuery;

public class Main {

    public static void main(String[] args) throws IOException {

        // --------------------------- INPUT CHECK ---------------------------
        if (args.length < 3) {
            System.err.println("Usage: java Main <mapped.txt> <propertyMap.txt> <idealSchema.rdf>");
            return;
        }

        String mappedFile = args[0];
        String propertyMapFile = args[1];
        String idealSchemaRDF = args[2];

        // --------------------------- INIT ---------------------------
        PatternsCreator patternCreator = new PatternsCreator();
        SchemaClassRecall schemaCR = new SchemaClassRecall();
        SchemaClassPrecision schemaCP = new SchemaClassPrecision();
        SchemaPropertyRecall schemaPR = new SchemaPropertyRecall();
        SchemaPropertyPrecision schemaPP = new SchemaPropertyPrecision();
        InstanceClassPrecision instanceCP = new InstanceClassPrecision();
        InstanceClassRecall instanceCR = new InstanceClassRecall();
        InstancePropertyPrecision instancePP = new InstancePropertyPrecision();
        InstancePropertyRecall instancePR = new InstancePropertyRecall();

        // ---------------------- PATTERN & SCHEMA EXTRACTION ----------------------
        ArrayList<Pattern> patterns = patternCreator.createPatterns1(mappedFile, propertyMapFile);
        SchemaExtractionBySPARQLQuery schemaExtractor = new SchemaExtractionBySPARQLQuery();
        ArrayList<RDFClass> schemaClasses = schemaExtractor.extractSchemaInformation(idealSchemaRDF);
        PropertyInstanceExtraction propertyExtractor = new PropertyInstanceExtraction();
        ArrayList<String> properties = propertyExtractor.extract(idealSchemaRDF);

        // --------------------------- SCHEMA METRICS ---------------------------
        double schemaClassPrecision = schemaCP.getSchemaClassPrecision(patterns, schemaClasses, 3.0);
        double schemaClassRecall = schemaCR.getSchemaClassRecall(patterns, schemaClasses);
        double schemaClassF1 = f1(schemaClassPrecision, schemaClassRecall);

        double schemaPropertyPrecision = schemaPP.getSchemaPropertyPrecision(patterns, schemaClasses); // double-check method name
        double schemaPropertyRecall = schemaPR.getSchemaPropertyRecall(patterns, schemaClasses);
        double schemaPropertyF1 = f1(schemaPropertyPrecision, schemaPropertyRecall);

        double overallSchemaF1 = (schemaClassF1 + schemaPropertyF1) / 2.0;

        // --------------------------- INSTANCE METRICS ---------------------------
        int totalInstances = patterns.size();  // estimate total instances
        double instanceClassPrecision = instanceCP.getInstanceClassPrecision(patterns, schemaClasses, totalInstances);
        double instanceClassRecall = instanceCR.getInstanceClassRecall(patterns, idealSchemaRDF);
        double instanceClassF1 = f1(instanceClassPrecision, instanceClassRecall);

        double instancePropertyPrecision = instancePP.getInstancePropertyPrecision(patterns, properties);
        double instancePropertyRecall = instancePR.getInstancePropertyRecall(patterns, properties);
        double instancePropertyF1 = f1(instancePropertyPrecision, instancePropertyRecall);

        double overallInstanceF1 = (instanceClassF1 + instancePropertyF1) / 2.0;

        // --------------------------- HTML OUTPUT ---------------------------
        String schemaHtml = buildHtmlTable("Schema Metrics", new String[][] {
            {"SchemaClassPrecision", "SchemaClassRecall", "SchemaClassF1", 
             "SchemaPropertyPrecision", "SchemaPropertyRecall", "SchemaPropertyF1", "OverallSchemaF1"},
            {format(schemaClassPrecision), format(schemaClassRecall), format(schemaClassF1),
             format(schemaPropertyPrecision), format(schemaPropertyRecall), format(schemaPropertyF1), format(overallSchemaF1)}
        });

        String instanceHtml = buildHtmlTable("Instance Metrics", new String[][] {
            {"InstanceClassPrecision", "InstanceClassRecall", "InstanceClassF1",
             "InstancePropertyPrecision", "InstancePropertyRecall", "InstancePropertyF1", "OverallInstanceF1"},
            {format(instanceClassPrecision), format(instanceClassRecall), format(instanceClassF1),
             format(instancePropertyPrecision), format(instancePropertyRecall), format(instancePropertyF1), format(overallInstanceF1)}
        });

        String fullHtml = "<html><head><title>Metric Results</title>"
                + "<style>body{font-family:sans-serif;} table{margin-bottom:30px;} th, td{text-align:center;}</style>"
                + "</head><body><h1>Evaluation Results</h1>"
                + schemaHtml + instanceHtml + "</body></html>";

        try (PrintWriter out = new PrintWriter(new FileWriter("results.html"))) {
            out.println(fullHtml);
            System.out.println("✅ Results written to results.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------------------ HELPERS ------------------------

    // Format double to 3 decimal places
    private static String format(double val) {
        return new DecimalFormat("##.###").format(val);
    }

    // Calculate F1-score from precision & recall
    private static double f1(double precision, double recall) {
        if (precision + recall == 0) return 0.0;
        return 2 * ((precision * recall) / (precision + recall));
    }

    // Build HTML table for given title and 2D rows
    private static String buildHtmlTable(String title, String[][] rows) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>").append(title).append("</h2>");
        html.append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>");

        for (int i = 0; i < rows.length; i++) {
            html.append("<tr>");
            for (String cell : rows[i]) {
                if (i == 0) {
                    html.append("<th>").append(cell).append("</th>");
                } else {
                    html.append("<td>").append(cell).append("</td>");
                }
            }
            html.append("</tr>");
        }

        html.append("</table>");
        return html.toString();
    }
}
