# Advanced Query Optimization System

## Overview
This project is dedicated to optimizing conjunctive queries, evaluating complex SQL-like queries, and implementing advanced query optimization techniques. The system is structured into three main components: Conjunctive Query Minimization, Query Evaluation, and Query Optimization. Each component is designed to handle specific aspects of database query processing, improving efficiency and reducing computational overhead.

## Components

### 1. Conjunctive Query Minimization
This module focuses on minimizing the complexity of conjunctive queries. It includes functions to parse, simplify, and save queries, ensuring that only necessary atoms are present, avoiding redundancy and inefficiencies.

**Key Functions:**
- **minimizeConjunctiveQuery**: Main method that orchestrates the query minimization process.
- **parseInputQuery**: Parses the input query and extracts essential elements.
- **minimizeQueryBody**: Iteratively removes redundant atoms from the query.
- **removeDuplicateAtoms**: Eliminates duplicate atoms to further simplify the query.
- **saveMinimizedQueryToFile**: Outputs the minimized query to a specified file.

### 2. Query Evaluation
This component is responsible for creating an optimized query plan that efficiently handles both join and non-join conditions.

**Query Plan Structure:**



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





### 3. Query Optimization
Focuses on optimizing query execution by implementing advanced techniques like selection and projection pushdown.

**Challenges and Implementation:**
- **Selection Pushdown**: Implemented to reduce the number of tuples early in the query process.
- **Projection Pushdown**: Attempted implementation to pass only necessary terms through the projection operator, though reverted due to complexities.

## Getting Started

### Prerequisites
- Ensure you have a Python environment setup with necessary libraries installed.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-github-username/your-repo-name.git
