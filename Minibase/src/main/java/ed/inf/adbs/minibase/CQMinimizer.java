package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class CQMinimizer {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        minimizeConjunctiveQuery(inputFile, outputFile);
    }

    /**
     * To perform the query minimization the parseInputQuery method reads the input query from the file and extracts
     * the variables in the head of the query. The minimizeQueryBody method performs the query minimization by applying
     * homomorphisms between atoms in the query body. The removeDuplicateAtoms method removes duplicate atoms
     * from the query body. The saveMinimizedQueryToFile method saves the minimized query to a file.
     *
     * Other methods include applyMapping, which applies a mapping between terms of an atom, areAtomsEqual,
     * which checks whether two atoms are equal, and findMapping, which finds a mapping between the terms of
     * two atoms. The createTermFromString method creates a new term from a string.
     *
     *
     */


    public static void minimizeConjunctiveQuery(String inputFile, String outputFile) {
        Set<String> headVars = new HashSet<>();
        Query inputQuery = parseInputQuery(inputFile, headVars);

        if (inputQuery == null) return;

        Head queryHead = inputQuery.getHead();
        List<Atom> queryBody = inputQuery.getBody();

        queryBody = removeDuplicateAtoms(queryBody);
        queryBody = minimizeQueryBody(queryBody, headVars);

        Query minimizedQuery = new Query(queryHead, queryBody);
        System.out.println(minimizedQuery.toString());
        saveMinimizedQueryToFile(minimizedQuery, outputFile);
    }


    private static Query parseInputQuery(String inputFile, Set<String> headVars) {
        Query inputQuery = null;

        try {
            inputQuery = QueryParser.parse(Paths.get(inputFile));
            Head queryHead = inputQuery.getHead();

            for (Term t : queryHead.getVariables()) {
                headVars.add(t.toString());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }

        return inputQuery;
    }


    private static List<Atom> minimizeQueryBody(List<Atom> queryBody, Set<String> headVars) {
        for (int i = 0; i < queryBody.size(); i++) {
            List<Atom> newQueryBody = new ArrayList<>(queryBody);
            newQueryBody.remove(i);

            HashMap<String, String> mapping = findMapping(queryBody, newQueryBody, headVars);
            boolean allAtomsInBody = true;

            for (int k = 0; k < queryBody.size(); k++) {
                Atom mappedAtom = applyMapping(queryBody.get(k), mapping);
                boolean atomFound = false;

                for (int j = 0; j < newQueryBody.size(); j++) {
                    if (areAtomsEqual(mappedAtom, newQueryBody.get(j))) {
                        atomFound = true;
                        break;
                    }
                }

                if (!atomFound) {
                    allAtomsInBody = false;
                    break;
                }
            }
            if (allAtomsInBody) {
                queryBody = newQueryBody;
                i--;
            }
        }
        return queryBody;
    }



    private static void saveMinimizedQueryToFile(Query minimizedQuery, String outputFile) {
        String str = minimizedQuery.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Atom applyMapping(Atom atom, HashMap<String, String> mapping) {
        if (!(atom instanceof RelationalAtom)) {
            return null;
        }

        RelationalAtom relationAtom = (RelationalAtom) atom;
        List<Term> oldTerms = relationAtom.getTerms();
        List<Term> newTerms = new ArrayList<>();

        for (int i = 0; i < oldTerms.size(); i++) {
            Term term = oldTerms.get(i);
            String termStr = term.toString();
            String newTermStr = mapping.get(termStr);
            if (newTermStr == null) {
                newTermStr = termStr;
            }
            Term newTerm = createTermFromString(newTermStr);
            newTerms.add(newTerm);
        }

        return new RelationalAtom(relationAtom.getName(), newTerms);
    }


    private static boolean areAtomsEqual(Atom atom1, Atom atom2) {
        if (atom1.getClass() != atom2.getClass()) {
            return false;
        }
        if (!(atom1 instanceof RelationalAtom) || !(atom2 instanceof RelationalAtom)) {
            return false;
        }

        RelationalAtom relationalAtom1 = (RelationalAtom) atom1;
        RelationalAtom relationalAtom2 = (RelationalAtom) atom2;

        if (!relationalAtom1.getName().equals(relationalAtom2.getName())) {
            return false;
        }

        List<Term> terms1 = relationalAtom1.getTerms();
        List<Term> terms2 = relationalAtom2.getTerms();

        if (terms1.size() != terms2.size()) {
            return false;
        }

        for (int i = 0; i < terms1.size(); i++) {
            if (!terms1.get(i).toString().equals(terms2.get(i).toString())) {
                return false;
            }
        }

        return true;
    }


    private static HashMap<String, String> findMapping(List<Atom> queryBody, List<Atom> newQueryBody, Set<String> headVars) {
        HashMap<String, String> map = new HashMap<>();

        for (Atom atom1 : queryBody) {
            for (Atom atom2 : newQueryBody) {
                RelationalAtom relationalAtom1 = (RelationalAtom) atom1;
                RelationalAtom relationalAtom2 = (RelationalAtom) atom2;

                if (!relationalAtom1.getName().equals(relationalAtom2.getName())) {
                    continue;
                }

                List<Term> terms1 = relationalAtom1.getTerms();
                List<Term> terms2 = relationalAtom2.getTerms();
                HashMap<String, String> possMap = new HashMap<>();

                for (int i = 0; i < terms1.size(); i++) {
                    Term term1 = terms1.get(i);
                    Term term2 = terms2.get(i);
                    String term1Str = term1.toString();
                    String term2Str = term2.toString();

                    if (headVars.contains(term1Str) && !headVars.contains(term2Str)) {
                        break;
                    }

                    if (term1 instanceof Constant) {
                        if (term2 instanceof Constant && !term1Str.equals(term2Str)) {
                            break;
                        } else if (term2 instanceof Variable && map.containsKey(term2Str) && !map.get(term2Str).equals(term1Str)) {
                            break;
                        } else if (term2 instanceof Variable && !map.containsKey(term2Str)) {
                            possMap.put(term2Str, term1Str);
                        }
                    } else if (term1 instanceof Variable) {
                        if (headVars.contains(term1Str)) {
                            if (term2 instanceof Constant) {
                                break;
                            } else {
                                possMap.put(term1Str, term2Str);
                            }
                        } else {
                            possMap.put(term1Str, term2Str);
                        }
                    }
                }

                if (possMap.size() == terms1.size()) {
                    for (String key : possMap.keySet()) {
                        map.put(key, possMap.get(key));
                    }
                }
            }
        }

        return map;
    }


    private static List<Atom> removeDuplicateAtoms(List<Atom> queryBody) {
        if (queryBody == null) {
            return new ArrayList<>();
        }
        List<Atom> uniqueAtoms = new ArrayList<>();
        for (int i = 0; i < queryBody.size(); i++) {
            Atom atom1 = queryBody.get(i);
            boolean found = false;
            for (int j = 0; j < uniqueAtoms.size(); j++) {
                Atom atom2 = uniqueAtoms.get(j);
                if (areAtomsEqual(atom1, atom2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                uniqueAtoms.add(atom1);
            }
        }
        return uniqueAtoms;
    }


    private static Term createTermFromString(String termStr) {
        if (termStr.matches("'(\\w|\\s)+'")) {
            return new StringConstant(termStr.substring(1, termStr.length() - 1));
        } else if (termStr.matches("\\d+")) {
            return new IntegerConstant(Integer.parseInt(termStr));
        } else {
            return new Variable(termStr);
        }
    }


    public static void parsingExample(String filename) {

        try {
            Query query = QueryParser.parse(Paths.get(filename));
            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }
}

