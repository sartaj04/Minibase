1. Conjunctive Query Minimisation:

The main method, minimizeConjunctiveQuery, takes as input the path to an input file containing the CQ to be minimized,
and the path to an output file where the minimized query will be written. It first reads in the input query using the
parseInputQuery method, which extracts the variables in the head of the query and returns the parsed query. If parsing
fails, it returns null.

The minimizeQueryBody method then takes the query body of the parsed query and iteratively removes each atom to check
if the remaining atoms are still satisfied by the original query. It does this by attempting to find a homomorphism between
the original query and the new query without the removed atom. If the new query can be satisfied by the original query, the
removed atom is deemed unnecessary and is not included in the minimized query.

The removeDuplicateAtoms method removes any duplicate atoms in the query body to simplify the query. Finally, the
saveMinimizedQueryToFile method writes the minimized query to the output file.

The findMapping method is used to find a mapping between the terms of two atoms. It first checks that the atoms have
the same relation name. Then, for each term in the atoms, it checks if it can be mapped to a corresponding term in the
other atom based on certain conditions (such as whether it is a head variable, constant or variable).

The applyMapping method is used to apply a mapping between the terms of an atom. It replaces each term in the atom with
the corresponding term in the mapping.

The areAtomsEqual method checks whether two atoms are equal by checking if they have the same relation name and same terms.

The createTermFromString method creates a new term object from a string, depending on whether the string represents a constant,
integer, or variable.


2. Query Evaluation:

For Query Evaluation a query plan is made which segregates the body predicates into join and non-join conditions. It then
constructs an optimized plan to perform selections and joins in a suitable order to efficiently produce the query results
specified by the Head and body predicates.




 Projection or GroupBy Operator are called based on presence of aggregation in Head..................
                               |
       Join Operator( Based on conditions type of join is decided).............................
               /                                 \                            \
              /                                   \                            \
             /                                     \                            \
 if select then selection                           \                            \
 operator or scan operator                if select then selection              Table3........
            /                             operator or scan operator
           /                                           \
         Table1                                       Table2                     .................


 The working of Each operator is discussed inside the Classes made in the form of comments.




 3. Query Optimization:

 For Query Optimization, the Selection pushdown was implemented. I've also tried for Projection Pushdown through passing only
 the required terms as one of argument in projection operator and adding a boolean argument to determine if it's final projection
 to get only output variables when flag is active otherwise to project from required variables but faced with lot of bugs during
 implementation so had to make it back to previous version due to time constraint.