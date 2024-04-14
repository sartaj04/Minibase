package ed.inf.adbs.minibase.base;
import java.util.*;

public class SelectionOperator extends Operator {
    private ScanOperator childOperator;

    private List terms;
    private List<ComparisonAtom> nonjoinatoms;
    private RelationalAtom relatoms;

    private List tuples = new ArrayList<>();
    private List ngss = new ArrayList<>();
    private Tuple t;
    private int in = 0;
    /**
     * scOp: a ScanOperator object, which is used to retrieve the tuples to be selected from a database table.
     * nonjoinatoms: a list of ComparisonAtom objects, which represent comparison conditions for the selection operation that involve only one table.
     * relatoms: a RelationalAtom object, which represents the selection condition that involves multiple tables.
     */


    public SelectionOperator(ScanOperator scOp, List nonjoinatoms, RelationalAtom relatoms ) {
        super();
        this.childOperator = scOp;
        this.nonjoinatoms = nonjoinatoms;
        this.relatoms = relatoms;
//        int i = 0;
//        this.t= this.getNextTuplelist(in);

    }

    /**
     *  This method returns the next tuple from the relation. It calls the getNextTuplelist method to retrieve the next tuple, increments the in variable, and returns the retrieved tuple.
     */
    @Override
    public Tuple getNextTuple(){
//        System.out.println(in);
        this.t=getNextTuplelist(in);
        in++;
        Tuple returntuple= this.t;
        return t;

//        Tuple returntuple = this.t;
//        in = in + 1;
//        this.t = this.getNextTuplelist(in);
//        return returntuple;

    }
    /**
     * This method resets the SelectionOperator to its initial state, which involves resetting the childOperator and in variables.
     */
    @Override
    public void reset() {
        //            this.reader.close();
//            this.reader = new BufferedReader(new FileReader(DatabaseCatalog.getFilePath(databaseDir, relation)));
        in = 0;
//        this.t = this.getNextTuplelist(in);
    }

    /**
     * This method is used to retrieve the next tuple from the relation.
     *
     * It takes an integer parameter ind representing the index of the next tuple to be retrieved from the tuples list.
     *
     * This method retrieves tuples from the relation that satisfy the conditions specified by the nonjoinatoms and
     * relatoms parameters.
     *
     *The getNextTuplelist method starts by getting the schema of the child operator and initializing some variables.
     *
     * It then iterates through each relational atom and its terms to identify any constant terms and their positions.
     *
     * It then iterates through each tuple retrieved from the child operator and applies the constant term filters,
     * storing the filtered tuples in a list.
     *
     * It then iterates through each non-join comparison atom, identifies the terms to be compared,
     * and applies the comparison operator to filter the tuples further, storing the filtered tuples in another list.
     *
     * Finally, it returns the tuple specified by the ind parameter, or null if the list of filtered tuples is empty.
     */
    public Tuple getNextTuplelist(int ind) {
//        this.childOperator = childOperator;
        List<String> schema = childOperator.getSchema();
//        int bodyLen = body.size();
        int schemaLen = schema.size();
        HashMap<String,List<Integer>> termvalue = new HashMap<String, List<Integer>>();
        String t1=null,t2=null;
//        ComparisonOperator op;
        Tuple line;
//        System.out.println(line.printTuple());
//        List tuples = new ArrayList();

        // 3-> {t1:4, t2:y, t3:z}
        HashMap<Integer,String> conditions = new HashMap<Integer,String>();

//        for(int i=0;i< relatoms.size();i++) {

            RelationalAtom b = (RelationalAtom) relatoms;
            terms = b.getTerms();
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < schemaLen; j++) {
                String t = terms.get(j).toString();
                boolean val = checkconstant(t);
                if (val) {
                    conditions.put(j, t);
                }
//                    list.add(val);
//                    list.add(j);
//                    termvalue.put(t,list);

            }


            while ((line = childOperator.getNextTuple()) != null) {
                if (conditions.size() != 0) {
                    for (int key : conditions.keySet()) {
                        String n = line.getValue(key).toString();
                        String n1 = n.replaceAll("\\s+", "");
                        if (n1.equals(conditions.get(key).toString())) {
                            tuples.add(line);
//                                    System.out.println("inside if" + line.printTuple());
                        }
                        if (!n1.equals(conditions.get(key).toString())) {
                            tuples.remove(line);
//                                    System.out.println("inside if" + line.printTuple());
                        }

                    }
                } else {
                    tuples.add(line);
//                            System.out.println("inside else" +line.printTuple());
                }
            }
            Set<String> set1 = new HashSet<>(tuples);
            tuples.clear();
            tuples.addAll(set1);



            for(int c1 =0; c1<nonjoinatoms.size(); c1++){

                ComparisonAtom c = nonjoinatoms.get(c1);
                t1 = c.getTerm1().toString();
                t2 = c.getTerm2().toString();
                t2 = t2.replaceAll("'", "");
                ComparisonOperator op = c.getOp();
//            Tuple line;
                List<Integer> indeces = new ArrayList<>();
//                System.out.println(terms);
//                System.out.println(tuples);
                for (int s = 0; s < terms.size(); s++) {
                    if (terms.get(s).toString().equals(t1)) {
                        indeces.add(s);
                    }
                    if (terms.get(s).toString().equals(t2)) {
                        indeces.add(s);
                    }
                }
                int ngs = tuples.size();
//                List ngss = new ArrayList<>();
                if(indeces.size()==1) {
//                 myVar = Level.MEDIUM;
                    for (int g = 0; g < ngs; g++) {
                        Tuple gs = (Tuple) tuples.get(g);
                        String gss = gs.getValue(indeces.get(0)).toString();
                        gss = gss.replaceAll("'","");
                        gss = gss.replaceAll(" ","");
                        try{
                            Integer ngss1 = Integer.parseInt(gss);
//                            Integer ngss2 = Integer.parseInt(gss2);
                            Integer t12 = Integer.parseInt(t2);
                            if(Compare(op,ngss1,t12)) {
                                ngss.add(gs);
                            }
                        }
                        catch (Exception e){
                            String ngss1 = gss;
//                            String ngss2 = gss2;
                            if(Compare(op,ngss1,t2)) {
                                ngss.add(gs);
                            }
                        }
//                        if(Compare(op,gss,t2)) {
//                            ngss.add(gs);
//                        }
                }
            }
                if(indeces.size()>1) {
//                 myVar = Level.MEDIUM;
//                    Object ngss1;
//                    Object ngss2;
                    for (int g = 0; g < ngs; g++) {
                        Tuple gs = (Tuple) tuples.get(g);
                        String gss = gs.getValue(indeces.get(0)).toString();
                        String gss2 = gs.getValue(indeces.get(1)).toString();
                        gss = gss.replaceAll("'","");
                        gss = gss.replaceAll(" ","");
                        gss2 = gss2.replaceAll("'","");
                        gss2 = gss2.replaceAll(" ","");
                        try{
                            Integer ngss1 = Integer.parseInt(gss);
                            Integer ngss2 = Integer.parseInt(gss2);
                            if(Compare(op,ngss1,ngss2)) {
                                ngss.add(gs);
                            }
                        }
                        catch (Exception e){
                            String ngss1 = gss;
                            String ngss2 = gss2;
                            if(Compare(op,ngss1,ngss2)) {
                                ngss.add(gs);
                            }
                        }

                    }
                }


        Set<String> set2 = new HashSet<>(ngss);
                ngss.clear();
                ngss.addAll(set2);

                try {
                    return (Tuple) ngss.get(ind);
                }
                catch (Exception E){
                    return null;
                }



    }
            return null;

}
    private boolean Compare(ComparisonOperator cmp, int x, int y) {
        if(cmp.toString().matches("=")) return x==y;
        if(cmp.toString().matches("!=")) return x!=y;
        if(cmp.toString().matches(">")) return x>y;
        if(cmp.toString().matches(">=")) return x>=y;
        if(cmp.toString().matches("<")) return x<y;
        if(cmp.toString().matches("<=")) return x<=y;
        return false;
    }

    private boolean Compare(ComparisonOperator cmp, String x, String y) {
        if(cmp.toString().matches("=")) return x.equals(y);
        if(cmp.toString().matches("!=")) return !x.equals(y);
        if(cmp.toString().matches(">")) return x.compareTo(y)>0;
        if(cmp.toString().matches(">=")) return x.compareTo(y)>=0;
        if(cmp.toString().matches("<")) return x.compareTo(y)<0;
        if(cmp.toString().matches("<=")) return x.compareTo(y)<=0;
        return false;
    }
    public static boolean checkconstant(String value) {
        if(value.contains("'")){
            return true;
        }
        else if(value.matches("-?\\d+(\\.\\d+)?")){
            return true;
        }
        return false;
    }
}



