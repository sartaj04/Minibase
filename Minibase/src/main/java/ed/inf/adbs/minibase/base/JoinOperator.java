package ed.inf.adbs.minibase.base;

import java.util.*;
//import ed.inf.adbs.minibase.base.*;

public class JoinOperator extends Operator{

    private Operator childOperator1;
    private Operator childOperator2;
    private List joinedtuples;
    private Tuple t;
    private Integer in = 0;
    private List vars;

    /**
     * The code loops through each joinAtom in joinAtoms, and checks if the values of atom1 and atom2
     * match the terms specified in the joinAtom. If there is a match, the indices of the values are
     * added to l1, and the comparison operator specified in the joinAtom is set to ni.
     *
     * The code checks for matches in both orders, nkc1 and nkc2 as well as nkc2 and nkc1.
     *
     * The l1 list is added to a list of indices indices, and the corresponding comparison operator is added
     * to a dictionary nk1, with the list of indices as the key.
     *
     * After creating a set of unique indices, the code checks if the indices list is empty. If it is empty, the
     * code performs a cross join between Op1 and Op2, and stores the resulting tuples in a hash map sss.
     * If the indices list is not empty, the code loops through each tuple from Op1 and Op2 and
     * checks if the conditions specified by the joinAtoms are met. If the conditions are met, the
     * tuples are merged into a single tuple, and the merged tuple is stored in the hash map sss.

     */
    public JoinOperator(Tuple atom1, Tuple atom2, Operator Op1, Operator Op2, List joinAtoms) {
        //super();
        this.childOperator1=Op1;
        this.childOperator2=Op2;
        List<ComparisonAtom> compAtoms = joinAtoms;
        List val1 = atom1.getValues();
        List val2 = atom2.getValues();
        Tuple mergedtuple = mergetuple(val1,val2);
        this.vars = mergedtuple.getValues();

        Tuple line;
        Tuple line2;
        List<Tuple> mergetuples = new ArrayList<>();
        Map< List,ComparisonOperator> nk1 = new HashMap<>();
//        Map<String, List> nk2 = new HashMap<>();
        List<List<Integer>> indices = new ArrayList<>();
//        System.out.println(joinAtoms);

        for(int n = 0; n<val1.size(); n++){
            List<Integer> l1 = new ArrayList<>();
            for(int n1 =0; n1<val2.size();n1++){
                ComparisonOperator cn = ComparisonOperator.EQ;
                if(val1.get(n).toString().equals(val2.get(n1).toString())){

                    l1.add(n); l1.add(n1);
                }
                if(!indices.contains(l1)){
                    if(!l1.isEmpty()){
                        indices.add(l1);
                        nk1.put(l1,cn);
                    }


                }
//                l1.clear();
                for(int z =0; z<joinAtoms.size(); z++){
                    ComparisonAtom ca = (ComparisonAtom) joinAtoms.get(z);
                    String nkc1 = ca.getTerm1().toString();
                    String nkc2 = ca.getTerm2().toString();

                    if(val1.get(n).toString().equals(nkc1)&&val2.get(n1).toString().equals(nkc2)){
                        ComparisonOperator ni = ca.getOp();
                        l1.add(n); l1.add(n1);
                        if(!indices.contains(l1)){
                            if(!l1.isEmpty()){
                                indices.add(l1);
                                nk1.put(l1,ni);
                            }
//                            nk1.put(indices,ni);
                        }
                    }
                    if(val1.get(n).toString().equals(nkc2)&&val2.get(n1).toString().equals(nkc1)){
                        ComparisonOperator ni = ca.getOp();
                        l1.add(n); l1.add(n1);
                        if(!indices.contains(l1)){
                            if(!l1.isEmpty()){
                                indices.add(l1);
                                nk1.put(l1,ni);
                            }

                        }
                    }

                }
            }
        }
        Set<List<Integer>> set2 = new HashSet<>(indices);
        indices.clear();
        indices.addAll(set2);
//        System.out.println("Indices"+nk1);

        List bla = new ArrayList<>();
        HashMap<Integer,List<List>> sss = new HashMap();
        List listtup = new ArrayList<>();
        childOperator1.reset();
        childOperator2.reset();

        if(indices.size()==0) {
            while ((line = childOperator1.getNextTuple()) != null && line.getValues() != null) {
                while ((line2 = childOperator2.getNextTuple()) != null && line2.getValues() != null) {

                    Tuple m = mergetuple(line.getValues(), line2.getValues());
//                    System.out.println(m.printTuple());
                    if (sss.get(0) != null) {
                        sss.get(0).add(m.getValues());
                    } else {
                        sss.put(0, new ArrayList<>());
                        sss.get(0).add(m.getValues());
                    }

                }
                childOperator2.reset();
            }
            childOperator1.reset();

        }


        boolean exceptionCaught;
        do {
            exceptionCaught = false;
            while ((line = childOperator1.getNextTuple()) != null && line.getValues() != null) {
                while ((line2 = childOperator2.getNextTuple()) != null && line2.getValues() != null) {

//                    System.out.println("before join 1 "+line.printTuple());
//                    System.out.println("before join 2 "+line2.printTuple());
                    if(indices.size()>0) {
                        try {
                            for (int nks = 0; nks < indices.size(); nks++) {
//                                System.out.println(nks);

                                List lst = (List) indices.get(nks);
//                        sss.put(nks,new ArrayList<>());
                                ComparisonOperator cop = nk1.get(lst);
                                if (lst.size() != 0) {
                                    Integer con1 = (Integer) lst.get(0);
                                    Integer con2 = (Integer) lst.get(1);
//                                System.out.println(line.getValues());
//                            System.out.println(line2.getValues());
//                            System.out.println(con1);
//                            System.out.println(con2);


                                    if (line != null && line.getValue(con1) != null && line2 != null && line2.getValue(con2) != null) {
//                                        Tuple gs = (Tuple) tuples.get(g);
                                        String gss = line.getValue(con1).toString();
                                        String gss2 = line2.getValue(con2).toString();
                                        gss = gss.replaceAll("'","");
                                        gss = gss.replaceAll(" ","");
                                        gss2 = gss2.replaceAll("'","");
                                        gss2 = gss2.replaceAll(" ","");
                                        try{
                                            Integer ngss1 = Integer.parseInt(gss);
                                            Integer ngss2 = Integer.parseInt(gss2);
                                            if (Compare(cop, ngss1, ngss2)) {
                                                Tuple nkgs = mergetuple(line.getValues(), line2.getValues());
//                                                System.out.println("Aftermerge"+nkgs.printTuple());

                                                if (sss.get(nks) != null) {
                                                    sss.get(nks).add(nkgs.getValues());
                                                } else {
                                                    sss.put(nks, new ArrayList<>());
                                                    sss.get(nks).add(nkgs.getValues());
                                                }
                                            }
                                        }
                                        catch (Exception e){
                                            String ngss1 = gss;
                                            String ngss2 = gss2;
                                            if (Compare(cop, ngss1, ngss2)) {
                                                Tuple nkgs = mergetuple(line.getValues(), line2.getValues());

                                                if (sss.get(nks) != null) {
                                                    sss.get(nks).add(nkgs.getValues());
                                                } else {
                                                    sss.put(nks, new ArrayList<>());
                                                    sss.get(nks).add(nkgs.getValues());
                                                }
                                            }
                                        }

                                    }

                                }

                            }
                        } catch (Exception e) {
                            exceptionCaught = true;
                            break;
                        }
                    }

                }
                if (exceptionCaught) {
                    break;
                }
                childOperator2.reset();
            }
            childOperator1.reset();
        } while (exceptionCaught);

//        System.out.println(sss);




    List<List> final_tuples = getuniqueTuples(sss);
//    System.out.println(final_tuples);
//    for(int t=0;t<final_tuples.size();t++){
//        System.out.println(final_tuples.get(t));
//    }
    this.joinedtuples= final_tuples;
//    this.t = new Tuple(this.getTuplefromList(in));

    }

    /**
     * Returns next joined tuple
     *
     */
    @Override
    public Tuple getNextTuple(){
        this.t= new Tuple(getTuplefromList(in)) ;
        in++;
        Tuple returntuple= this.t;
        return returntuple;
//        Tuple returntuple = this.t;
////        System.out.println(returntuple.printTuple());
//        in = in+1;
//
//
//        this.t = new Tuple(this.getTuplefromList(in));
//        return returntuple;

    }

    /**
     * Returns joined vars
     *
     */
    public List getvarlist() {
        //            this.reader.close();
//            this.reader = new BufferedReader(new FileReader(DatabaseCatalog.getFilePath(databaseDir, relation)));
        return this.vars;
    }

    /**
     * resets the index to 0
     */
    @Override
    public void reset() {
        //            this.reader.close();
//            this.reader = new BufferedReader(new FileReader(DatabaseCatalog.getFilePath(databaseDir, relation)));
        in = 0;
//        this.t = new Tuple(this.getTuplefromList(in));
    }

    /**
     * Uses Joinedtuples list made in above constructor and returns value at particular index

     */
    public List getTuplefromList(Integer ind){
        if (ind<joinedtuples.size()){
            return (List) this.joinedtuples.get(ind);
        }
        else{
            return null;
        }

    }

    /**
     * The method first initializes an empty list unique_tuples.
     * It then iterates over the keys of the input HashMap, and for each key, it retrieves the corresponding list from the HashMap using the get() method.
     *
     * If unique_tuples is empty, the method adds all the tuples in the retrieved list to unique_tuples
     * using the addAll() method. Otherwise, the method uses the retainAll() method to remove any tuples
     * from unique_tuples that are not present in the retrieved list. This operation ensures that unique_tuples
     * only contains tuples that are common to all the lists in the input HashMap.
     *
     * After iterating over all the keys in the input HashMap,
     * the method removes any duplicates from unique_tuples using a HashSet,
     * and returns the resulting list of unique tuples.
     */
    public List getuniqueTuples(HashMap<Integer,List<List>> tuples){
        List unique_tuples = new ArrayList<>();

        for (Integer key : tuples.keySet()){
            List lst = (List) tuples.get(key);
            if(unique_tuples.isEmpty()){
                unique_tuples.addAll(lst);
            }
            else{
                unique_tuples.retainAll(lst);
            }

        }
        Set<List> set1 = new HashSet<>(unique_tuples);
        unique_tuples.clear();
        unique_tuples.addAll(set1);
        return unique_tuples;
    }
/**
 * mergetuple which takes two lists (tuple1 and tuple2) as input,
 * merges them into a single list and returns a new Tuple object that contains the merged values.
 */
    public Tuple mergetuple(List tuple1,List tuple2){
//        List val1 = tuple1.getValues();
//        List val2 = tuple2.getValues();
        List m = new ArrayList<>();
        m.addAll(tuple1);
        m.addAll(tuple2);
        Tuple merged = new Tuple(m);
        return merged;
    }
    private boolean Compare(ComparisonOperator cmp, int x, int y) {
        if (cmp == null) {
            throw new IllegalArgumentException("ComparisonOperator cannot be null.");
        }
        if(cmp.toString().matches("=")) return x==y;
        if(cmp.toString().matches("!=")) return x!=y;
        if(cmp.toString().matches(">")) return x>y;
        if(cmp.toString().matches(">=")) return x>=y;
        if(cmp.toString().matches("<")) return x<y;
        if(cmp.toString().matches("<=")) return x<=y;
        return false;
    }

    private boolean Compare(ComparisonOperator cmp, String x, String y) {
        if (cmp == null) {
            throw new IllegalArgumentException("ComparisonOperator cannot be null.");
        }
        if (x == null || y == null) {
            throw new IllegalArgumentException("Comparison terms cannot be null.");
        }
        if(cmp.toString().matches("=")) return x.equals(y);
        if(cmp.toString().matches("!=")) return !x.equals(y);
        if(cmp.toString().matches(">")) return x.compareTo(y)>0;
        if(cmp.toString().matches(">=")) return x.compareTo(y)>=0;
        if(cmp.toString().matches("<")) return x.compareTo(y)<0;
        if(cmp.toString().matches("<=")) return x.compareTo(y)<=0;
        return false;
    }
}

