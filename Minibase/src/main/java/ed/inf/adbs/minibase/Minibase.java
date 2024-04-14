package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
//import ed.inf.adbs.minibase.base.ProjectionOperator;
import ed.inf.adbs.minibase.base.SelectionOperator;
import ed.inf.adbs.minibase.parser.QueryParser;
import ed.inf.adbs.minibase.base.JoinOperator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory database system
 *
 */
public class Minibase {


    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }

        String databaseDir = args[0];
        String inputFile = args[1];
        String outputFile = args[2];

        evaluateCQ(databaseDir, inputFile, outputFile);

        //parsingExample(inputFile);
    }

    /**
     * evaluate CQ calls Query Planner Function to get the final operator which will either be a Projection or Aggregate Operator.
     * Outputs into csv the output of the evaluation/optimization process
     */

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
        // TODO: add your implementation
        try {
            Query query = QueryParser.parse(Paths.get(inputFile));
//            System.out.println(databaseDir);

//            System.out.println("Entire query: " + query);
            Head head = query.getHead();
//            System.out.println("Head: " + head);


            List<Atom> body = query.getBody();
            QueryPlan qp = new QueryPlan(head, body, databaseDir);
            Operator prop = qp.getProp();
            String eol = System.getProperty("line.separator");
            if(prop instanceof GroupBy){
                if(((GroupBy) prop).getM() != null){
                    Map<List,Integer>  m = ((GroupBy) prop).getM();
                    StringWriter output = new StringWriter();


                    try (Writer writer = new FileWriter(outputFile)) {
                        for (Map.Entry<List,Integer> entry : m.entrySet()) {
                            String str =  entry.getValue().toString();
                            writer.append((CharSequence) entry.getKey())
                                    .append(',')
                                    .append(str)
                                    .append(eol);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }

                }
                else{
                    Integer s = ((GroupBy) prop).getVal();
                    String s1 = s.toString();
                    try (Writer writer = new FileWriter(outputFile)) {
                        writer.append(s1);
                    }
                }
            }
            if(prop instanceof ProjectionOperator){
                Tuple line;
                try (Writer writer = new FileWriter(outputFile)) {
                    while ((line = prop.getNextTuple()) != null) {
                        List<String> lst= line.getValues();
                        for(int l=0; l<lst.size(); l++){
                            writer.append(lst.get(l));
                            if(l!= lst.size()-1){
                                writer.append(',');
                            }
                        }
                        writer.append(eol);
                    }
                }
                catch (Exception E){

                }
            }

        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void parsingExample(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w), z < w");
            // Query query = QueryParser.parse("Q(SUM(x * 2 * x)) :- R(x, 'z'), S(4, z, w), 4 < 'test string' ");

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
