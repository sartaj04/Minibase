package ed.inf.adbs.minibase.base;

import java.util.*;

public class GroupBy extends Operator{
    private Head head; private List relAtoms; private Operator relop1; private List relterms1;
    private int val;
    private Map m;

    /**
     * The GroupBy class is a subclass of the Operator class, which represents a generic operator
     * in a query execution plan. The GroupBy class takes as input a Head object, a list of RelationalAtom
     * objects, an Operator object, and a list of Term objects.
     */

    public GroupBy(Head head, List relAtoms, Operator relop1, List relterms1){

        this.head = head;
        this.relAtoms = relAtoms;
        this.relop1 = relop1;
        this.relterms1 = relterms1;
        groupbylist();
    }

    /**The groupbylist() method is the main method of the GroupBy class.
     * It first extracts the aggregate function and its terms from the Head object,
     * along with the variables that appear in the head.
     *
     * It then extracts the indices of the variables that appear in the RelationalAtom objects,
     * which will be used to group the tuples.
     *
     * If the aggregate function is a simple sum of a single integer constant, and there are no variables in the head,
     * the method computes the sum directly by iterating over the tuples returned by the input operator. Otherwise,
     * the method groups the tuples according to the specified indices and computes the aggregate value for each
     * group by multiplying the values of the terms specified in the aggregate function.
     *
     * Finally, the method returns a map that maps each group to its aggregate value.
     * The getNextTuple() method of the GroupBy operator returns each group and its aggregate value as a single tuple.
     */

    public List groupbylist() {
        SumAggregate sum = head.getSumAggregate();
        System.out.println(sum);
        System.out.println(sum.getProductTerms());
        System.out.println("headvar"+head.getVariables());
        System.out.println(sum.toString());

        List<Term> prodterms = sum.getProductTerms();
        System.out.println("prodterms"+prodterms);
        List head_var = head.getVariables();
        List indeces = new ArrayList<>();
        List prodindeces = new ArrayList<>();
        List projectvar = new ArrayList<>();
        projectvar.addAll(head_var);
        projectvar.addAll(prodterms);
        System.out.println("projectvar"+projectvar);
        for (int b = 0; b < relAtoms.size(); b++) {
            try {
                RelationalAtom bo = (RelationalAtom) relAtoms.get(b);
                List<Term> terms = bo.getTerms();
                for(int i =0; i<head_var.size();i++) {

                    for (int t = 0; t < terms.size(); t++) {
                        if (head_var.get(i).toString().equals(terms.get(t).toString())) {
                            if(!indeces.contains(t)) {

                                indeces.add(t);

                            }
                        }
                    }
                }
                for (int t = 0; t < relterms1.size(); t++) {
                    for(int p = 0; p < prodterms.size(); p++){
                        if (prodterms.get(p).toString().equals(relterms1.get(t).toString())) {
                            if(!prodindeces.contains(t)) {

                                prodindeces.add(t);

                            }

                        }

                    }

                    }


            }
            catch (Exception E) {
                continue;
            }

        }
//    Set<String> set1 = new HashSet<>(indeces);
//    indeces.clear();
//    indeces.addAll(set1);
        System.out.println("output"+indeces);
        System.out.println("prod"+prodindeces);
        List list1 = new ArrayList<>();
        List list2 = new ArrayList<>();
        Tuple nk;
        relop1.reset();

        if(isintconstant(prodterms.get(0).toString())&&prodterms.size()==1&&head_var.isEmpty()){
            Integer i = Integer.parseInt(prodterms.get(0).toString());
            int t =0;

            while((nk = relop1.getNextTuple()) != null && nk.getValues() != null) {
                t = t+i;
            }
            System.out.println(t);
            this.val = t;


        }
        else{

            Map<Tuple,Integer> prodlist = new HashMap<Tuple,Integer>();
            List<Tuple> tuples = new ArrayList<>();
            while((nk = relop1.getNextTuple()) != null && nk.getValues() != null) {
                 tuples.add(nk);
            }
            relop1.reset();
//            int i = 0;
//            Map<String,Integer> sumlist = new HashMap<String,Integer>();
            for(int t=0;t< tuples.size();t++) {

                    Tuple t1 = tuples.get(t);

                    if(prodindeces.size()>0) {
                        for (int pr = 0; pr < prodindeces.size(); pr++) {
                            String pr1 = t1.getValue((Integer) prodindeces.get(pr));

                            pr1 = pr1.replaceAll("'", "");
                            pr1 = pr1.replaceAll(" ", "");

                            Integer pri = Integer.parseInt(pr1);
                            if (prodlist.get(t1) != null) {

                                prodlist.put(t1, prodlist.get(t1) * pri);
//                                i++;


                            } else {
                                prodlist.put(t1, pri);
//                                i = 1;
                            }
//                    i++;
                        }
                    }

            }
                        System.out.println(prodlist);
//            List ins = new ArrayList<>();
            List tup = new ArrayList<>();
            Map finl = new HashMap<List,Integer>();
            Integer sumy = 0;

                if (indeces.size()>0) {
                    for (Tuple pl : prodlist.keySet()) {
                        for (int il = 0; il < indeces.size(); il++) {
                            String vn = pl.getValue((Integer) indeces.get(il));
                            if (finl.get(vn) != null) {
                                Integer fin = (Integer) prodlist.get(pl) + (Integer) finl.get(vn);
                                finl.put(vn, fin);
                            } else {
                                finl.put(vn, prodlist.get(pl));
//                    finl.get(tup);
//                    prodlist.put(t1, prodlist.get(t1) + pri);
                            }
                        }

                    }
                    this.m = finl;
                }
                else {
                        for(Tuple pl : prodlist.keySet()){
                    for (int il = 0; il < prodindeces.size(); il++) {
                        String vn = pl.getValue((Integer) prodindeces.get(il));
                        if (finl.get(vn) != null) {
                            Integer fin = (Integer) prodlist.get(pl) + (Integer) finl.get(vn);
                            finl.put(vn, fin);
                        } else {
                            finl.put(vn, prodlist.get(pl));
//                    finl.get(tup);
//                    prodlist.put(t1, prodlist.get(t1) + pri);
                        }
                    }
                }
                    System.out.println(finl);

                    for (Object v : finl.values()) {
                        Integer g = (Integer) v;

                            sumy = sumy + g;


//                        System.out.println(g);

                    }
                    this.val = sumy;
                    System.out.println(sumy);
            }

            System.out.println(finl);
            Set<String> set2 = new HashSet<>(list2);
            list2.clear();
            list2.addAll(set2);

        }


        return null;
    }
    public static boolean isintconstant(String value) {
//        if(value.contains("'")){
//            return true;
//        }
        if(value.matches("-?\\d+(\\.\\d+)?")){
            return true;
        }
        return false;
    }

    public Map getM() {
        return m;
    }

    public int getVal() {
        return val;
    }
}
