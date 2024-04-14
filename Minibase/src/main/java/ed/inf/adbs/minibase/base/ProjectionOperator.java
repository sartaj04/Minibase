package ed.inf.adbs.minibase.base;
import ed.inf.adbs.minibase.base.*;
//import ed.inf.adbs.minibase.base.SelectionOperator;
import java.io.BufferedReader;
import java.util.*;

public class ProjectionOperator extends Operator {
    private ed.inf.adbs.minibase.base.SelectionOperator childOperator;

    private List terms;


    private List head;
    private List<RelationalAtom> relAtoms;
    private Operator relop1;
    private List relterms1;

    private int n = 0;
    private Tuple t;

    /**
     * The ProjectionOperator class represents an operator used in database query
     * processing to project only a subset of the attributes of a relation.
     * The constructor initializes the instance variables and creates a new Tuple object for the output of the operator.
     *
     */

    public ProjectionOperator(List head,List<RelationalAtom>relAtoms, Operator relop1, List relterms1) {
        super();
        this.head = head;
        this.relAtoms = relAtoms;
        this.relop1 = relop1;
        this.relterms1 = relterms1;
        t= new Tuple(ProjectionOperatorlist(n));

    }

    /**The getNextTuple() method is an overridden method from the superclass that retrieves
     * the next tuple from the operator's output.
     * In this implementation, the method updates the instance variables and
     * returns the previous tuple from the operator's output.
     */

    @Override
    public Tuple getNextTuple(){
        Tuple t1 = t;
        n=n+1;
        t = new Tuple(ProjectionOperatorlist(n));
        return t1;
    }

    /**
     * The method starts by iterating over the relational atoms in the input relAtoms list and extracting the terms of each atom.
     * It then compares the terms with the attributes in the input head list and relterms1 list,
     * and adds the index of the attribute to the indeces list if there is a match.
     *
     * The method then initializes two empty lists list1 and list2. It resets the input operator relop1 to the
     * beginning of the tuple stream using the reset() method. Then, it iterates over the tuples in the tuple stream,
     * extracts the values of the attributes at the indices in the indeces list, and adds them to list1.
     * Finally, it adds a copy of list1 to list2 and clears list1 for the next iteration.
     *
     * The method then removes any duplicates from list2 and returns the n1-th tuple in the list.
     * If the index n1 is greater than or equal to the length of list2, it returns null.
     */

    public List ProjectionOperatorlist(int n1) {
        this.childOperator = childOperator;
//        List nk = childOperator.getNextTuplelist(scop, Relation, body, head, databaseDir);
//    System.out.println(nk);

//        Head head;
        List head_var = head;
        List indeces = new ArrayList<>();



        for (int b = 0; b < relAtoms.size(); b++) {
            try {
                RelationalAtom bo = (RelationalAtom) relAtoms.get(b);
                terms = bo.getTerms();

                for(int i =0; i<head_var.size();i++) {
                    for (int t = 0; t < relterms1.size(); t++) {
                        if (head_var.get(i).toString().equals(relterms1.get(t).toString())) {
                            if(!indeces.contains(t)) {

                                indeces.add(t);

                            }
                        }
                    }
                    for (int t = 0; t < terms.size(); t++) {
                        if (head_var.get(i).toString().equals(terms.get(t).toString())) {
                            if(!indeces.contains(t)) {

                                indeces.add(t);

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
        List list1 = new ArrayList<>();
        List list2 = new ArrayList<>();
        Tuple nk;
        relop1.reset();

        while((nk = relop1.getNextTuple()) != null && nk.getValues() != null){
            for (int il = 0; il<indeces.size(); il++){
//                Tuple nk1 = (Tuple) nk.getValue(n);
                list1.add(nk.getValue((Integer) indeces.get(il)));

            }

//        Set<String> set3 = new HashSet<>(list1);
//        list1.clear();
//        list1.addAll(set3);
//        list2.add(list1);
            List in_list = new ArrayList<>();
            in_list.addAll(list1);
            list2.add(in_list);
            list1.clear();
        }
        Set<String> set2 = new HashSet<>(list2);
        list2.clear();
        list2.addAll(set2);


//        System.out.println(list2);
        if (n1<list2.size()){
            return (List) list2.get(n1);
        }
        else{
            return null;
        }

    }
}


