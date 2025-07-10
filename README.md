
# Quality Metrics For RDF Graph Summarization

This project provides an implementation of a Quality Evaluation Framework for RDF graph summaries. It takes as input the results of any RDF Graph Summarization algorithm and compares them with a reference (ideal) summary. It computes multiple quality metrics at two levels:
- Schema Level: Measures precision, recall, and F1-score for classes and their neighborhoods.
- Instance Level: Evaluates how well instances are covered and represented by the summary.
The framework outputs these metrics in an automated fashion and produces a readable HTML report.

## Setup:
This project requires Apache Jena. All required `.jar` files are already included in the `lib` folder.
To set up in Eclipse:
1. Download or clone the project.
2. Import it into Eclipse.
3. Right-click the project > Build Path > Configure Build Path.
4. Under Libraries, click Add JARs... and add all `.jar` files from the `lib` folder.
## Running the Project:
Before running the project, make sure you have(you have examples in Test_Files Folder):
1. A mapped summary file (e.g., `mapped.txt`): Each line represents a pattern.
2. A property mapping file (`propertyMap.txt`): Maps property URIs to IDs.
3. An ideal RDF schema file (`ideal.rdf`): The reference summary to compare against
 
For example, For the Knowledge patterns described in the following table.

| PatternId | class | Properties  | Instances
| -------- |-------------|-----|-----------
|1 | Painter  | fname, lname, paints | Picasso, Rembrandt
|2 |Painting  | exhibited |Woman, Guernica 
|3| Painting   | - | Abraham 
|4| Museum   | - |museum.es|

you should have one text file consists of four lines as following. <br />
1 &nbsp; 2 &nbsp; 3  &nbsp;4 (2) 1&nbsp;  2 <br />
5 &nbsp; 6 (2) 3 &nbsp; 4 <br />
5  (1) 5 <br />
7  (1) 6 <br />

Where the two following tables describe the two necessary property and instance hashMaps respectively. 

| Mapid | property URI        
| -------- |-------------
1 | Painter  
2 | fname
3| lname
4| paints 
5 |Painting  
6| exhibited 
7| Museum

| Mapid | Instance URI        
| -------- |-------------
1 | Picasso  
2 | Rembrandt
3| Woman
4| Guernica 
5 |Abraham  
6| museum.es

## How to Run
java www.etis.ensea.fr.main.EvaluationGUI <mapped.txt> <propertyMap.txt> <ideal.rdf>

Command Line Arguments:
mapped.txt — The summary output mapped to property and instance IDs (binary matrix format).
propertyMap.txt — The mapping of property URIs to integers.
ideal.rdf — The RDF file containing the ideal reference summary (RDF/XML format)
## Output
The program computes both schema and instance metrics, including precision, recall, and F1-score.

The results are displayed directly in a graphical user interface (GUI), which includes two sections:

1- Schema Metrics
2- Instance Metrics

Each section reports:
Precision
Recall
F1-score
Overall F1 score

You can visually inspect the evaluation results in the GUI without needing to open any separate files.


## Authors & Contributions
This tool was developed as part of the quality analysis framework for RDF summarization.
