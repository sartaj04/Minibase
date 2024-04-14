package ed.inf.adbs.minibase.base;

import java.util.*;

/**
 * The name of the first relational atom is extracted from the body list, which is
 * assumed to be the base relation. Then various data structures are initializes  to keep track of the
 * comparison and relational atoms in the body predicates, as well as information about join and
 * non-join conditions.
 *
 * Next, the code iterates through the body predicates, separating the relational and comparison atoms
 * into their respective data structures. For each comparison atom, the code determines whether it involves
 * a constant or a variable. If it involves a constant, the atom is added to the nonJoinAtoms map for the relevant
 * relation. If it involves two variables, the code tries to determine whether both variables appear in the same relation.
 * If they do, the atom is added to the nonJoinAtoms map for that relation. If they don't, the atom is added to the
 * joinAtoms map for the relation that contains both variables.
 *
 * After separating the atoms into join and non-join conditions, the code removes any empty collections from
 * the joinAtoms and nonJoinAtoms maps.
 *
 * Finally, the code constructs a query plan by iterating through the relational atoms in the body predicates.
 * For each relational atom, the code checks whether it has any non-join conditions or if it contains any constants.
 * If it does, the code creates a ScanOperator for the relation and applies the non-join conditions as a selection.
 * If it doesn't, the code creates a TableOperator for the relation.
 *
 * For each relation, the code also checks whether there are any join conditions involving that relation.
 * If there are, the code creates a JoinOperator that joins the relation with the other relations involved
 * in the join conditions. If there are multiple join conditions involving the same relation, the code
 * applies them sequentially using the JoinOperator.
 *
 * In summary, the query plan constructed by the code first separates the body predicates into join and non-join
 * conditions, and then constructs a plan that applies selection and joins in the appropriate order to efficiently
 * compute the results of the query represented by the Head and body predicates.
 */

public class QueryPlan {
    private Operator prop;

    public QueryPlan(Head head, List body, String databasedir) {


//        System.out.println("head: " + head);
//        System.out.println("body: " + body);

        RelationalAtom ra = (RelationalAtom) body.get(0);
        String relation = ra.getName();
        List<ComparisonAtom> compAtoms = new ArrayList<>();
        List<RelationalAtom> relAtoms = new ArrayList<>();
        Map<String, List<ComparisonAtom>> joinAtoms = new HashMap<>();
        Map<String, List<ComparisonAtom>> nonJoinAtoms = new HashMap<>();
        Map<String, List<String>> relaAtomsMap = new HashMap<>();
        Set<Term> joinvar = new HashSet<>();
        Set<Term> selvar = new HashSet<>();
        for (int i = 0; i < body.size(); i++) {
            try {
                RelationalAtom b = (RelationalAtom) body.get(i);
                relAtoms.add(b);
                List<String> bodyVar = new ArrayList<>();
                for (int k = 0; k < b.getTerms().size(); k++) {
                    bodyVar.add(b.getTerms().get(k).toString());
                }
                relaAtomsMap.put(b.getName(), bodyVar);
                joinAtoms.put(b.getName(), new ArrayList<>());
                nonJoinAtoms.put(b.getName(), new ArrayList<>());
            } catch (Exception ex) {
                compAtoms.add((ComparisonAtom) body.get(i));
            }
        }
        for (int i = 0; i < compAtoms.size(); i++) {
            ComparisonAtom c = compAtoms.get(i);
            Term t1 = c.getTerm1();
            Term t2 = c.getTerm2();

            ComparisonOperator op = c.getOp();
            if (t2.getClass() == StringConstant.class || t2.getClass() == IntegerConstant.class) {
                for (String key : relaAtomsMap.keySet()) {
                    if (relaAtomsMap.get(key).contains(t1.toString())) {
                        if (nonJoinAtoms.get(key) != null) {
                            nonJoinAtoms.get(key).add(c);
                        } else {
                            nonJoinAtoms.put(key, new ArrayList<>());
                            nonJoinAtoms.get(key).add(c);
                        }
//                        nonJoinAtoms.put(key,c);
                    }

                }

            } else {
                try {
                    String t1_1 = c.getTerm1().toString();
                    String t2_1 = c.getTerm2().toString();
                    for (String k : relaAtomsMap.keySet()) {
                        if (relaAtomsMap.get(k).contains(t1_1) && relaAtomsMap.get(k).contains(t2_1)) {
                            if (!nonJoinAtoms.get(k).contains(c)) {
                                if (nonJoinAtoms.get(k) != null) {
                                    nonJoinAtoms.get(k).add(c);
                                } else {
                                    nonJoinAtoms.put(k, new ArrayList<>());
                                    nonJoinAtoms.get(k).add(c);
                                }
//                            nonJoinAtoms.add(c);
                            }
//                            if (joinAtoms.get(k).contains(c)) {
//                                if (joinAtoms.get(k) != null) {
//                                    joinAtoms.get(k).remove(c);
//                                }
//                            else {
//                                joinAtoms.put(k, new ArrayList<>());
//                                joinAtoms.get(k).remove(c);
//                            }
//                            joinAtoms.remove(c);
//                            }
                        }
                        if (!(relaAtomsMap.get(k).contains(t1_1) && relaAtomsMap.get(k).contains(t2_1))) {
                            if (!nonJoinAtoms.get(k).contains(c) && !joinAtoms.get(k).contains(c)) {
                                if (joinAtoms.get(k) != null) {
                                    joinAtoms.get(k).add(c);
                                } else {
                                    joinAtoms.put(k, new ArrayList<>());
                                    joinAtoms.get(k).add(c);
                                }
//                            joinAtoms.add(c);
                            }
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
//        joinAtoms.values().removeAll(Collections.singleton());
        joinAtoms.entrySet().removeIf(e -> e.getValue() instanceof Collection && ((Collection) e.getValue()).isEmpty());
        nonJoinAtoms.entrySet().removeIf(e -> e.getValue() instanceof Collection && ((Collection) e.getValue()).isEmpty());
//        nonJoinAtoms.values().removeAll(Collections.singleton(null));
//        System.out.println("rela: " + relAtoms);
//        System.out.println("joinAtoms: " + joinAtoms);
//        System.out.println("nonJoinAtoms: " + nonJoinAtoms);

//        for(int r = 0; r< relAtoms.size();r++){
//
//
//        }

        int r = 0;
        int n = r + 1;
        if (relAtoms.size() > 0 ) {
                RelationalAtom r1 = relAtoms.get(r);
                String relname1 = r1.getName();
                Operator jop = null;
                Set<String> joinrelnameset = joinAtoms.keySet();
                Set<String> selrelnameset = nonJoinAtoms.keySet();
                List relterms1 = r1.getTerms();
                Tuple reltupl1 = new Tuple(relterms1);

                Boolean relsel1 = false;

                Operator relop1;
                for (int ri1 = 0; ri1 < relterms1.size(); ri1++) {
                    if (checkconstant(relterms1.get(ri1).toString())) {
                        relsel1 = true;
                    }
                }

                if (selrelnameset.contains(relname1) || relsel1) {
                    ScanOperator scop = new ScanOperator(databasedir, relname1);
                    List<ComparisonAtom> Compvalues1 = flattenvalues(nonJoinAtoms);
//                    System.out.println(Compvalues1);
                    relop1 = new SelectionOperator(scop, (List) Compvalues1, r1);


                } else {
                    relop1 = new ScanOperator(databasedir, relname1);


                }
                for (n=1;n< relAtoms.size();n++) {


                    if(relAtoms.size()>1){
                        RelationalAtom r2 = relAtoms.get(n);
                        String relname2 = r2.getName();
                        List relterms2 = r2.getTerms();
                        Tuple reltupl2 = new Tuple(relterms2);
                        Boolean relsel2 = false;
                        Operator relop2;
                        for (int ri2 = 0; ri2 < relterms2.size(); ri2++) {
                            if (checkconstant(relterms2.get(ri2).toString())) {
                                relsel2 = true;
                            }
                        }
                        if (selrelnameset.contains(relname2) || relsel2) {
                            ScanOperator scop2 = new ScanOperator(databasedir, relname2);
                            List<ComparisonAtom> Compvalues2 = flattenvalues(nonJoinAtoms);

//                        System.out.println(Compvalues2);
                            relop2 = new SelectionOperator(scop2, (List) Compvalues2, r2);


                        } else {
                            relop2 = new ScanOperator(databasedir, relname2);

                        }
                        List<ComparisonAtom> joinvalues = flattenvalues(joinAtoms);
//                    relop1.reset();
//                    relop2.reset();
                        jop = new JoinOperator(reltupl1, reltupl2, relop1, relop2, joinvalues);
                        jop.reset();
                        reltupl1 = mergetuple(relterms1,relterms2);
                        relterms1 = reltupl1.getValues();
                        relop1 = jop;
                    }


                }
            if(head.getSumAggregate() !=null){
//                        System.out.println(head.getSumAggregate());
                this.prop = new GroupBy(head, relAtoms, relop1, relterms1);

            }
            else{
                this.prop = new ProjectionOperator(head.getVariables(), relAtoms, relop1, relterms1);

            }
//

//                System.out.println(jop.getNextTuple().printTuple());



//            System.out.println(jop.getNextTuple().printTuple());

//            if()

        }
    }

    public Operator getProp() {
        return prop;
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
    public List flattenvalues(Map<String,List<ComparisonAtom>> h) {
        Collection<List<ComparisonAtom>> values = h.values();
        ArrayList<List<ComparisonAtom>> listOfValues = new ArrayList<List<ComparisonAtom>>(values);
        List<ComparisonAtom> Compvalues =
                listOfValues.stream()
                        .collect(ArrayList::new, List::addAll, List::addAll);
//        System.out.println(Compvalues);
        Set<ComparisonAtom> set1 = new HashSet<>(Compvalues);
        Compvalues.clear();
        Compvalues.addAll(set1);
        return Compvalues;
    }
    public Tuple mergetuple(List tuple1,List tuple2){
//        List val1 = tuple1.getValues();
//        List val2 = tuple2.getValues();
        List m = new ArrayList<>();
        m.addAll(tuple1);
        m.addAll(tuple2);
        Tuple merged = new Tuple(m);
        return merged;
    }

}
