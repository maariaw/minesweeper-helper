
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CSP<V, D> {
    private ArrayList<V> variables;
    private HashMap<V, ArrayList<D>> domains;
    private HashMap<V, ArrayList<Constraint<V, D>>> constraints;

    public CSP(ArrayList<V> variables, HashMap<V, ArrayList<D>> domains) {
        this.variables = variables;
        this.domains = domains;
        this.constraints = new HashMap<>();
        for (V variable : variables) {
            this.constraints.put(variable, new ArrayList<>());
            if (!this.domains.containsKey(variable)) {
                System.out.println("Every variable should have a domain assigned");
            }
        }
    }
    
    public void addConstraint(Constraint constraint) {
        ArrayList<V> vars = constraint.getVariables();
        for (V variable : vars) {
            if (!this.variables.contains(variable)) {
                System.out.println("Variable in constraint not in CSP");
            } else {
                this.constraints.get(variable).add(constraint);
            }
        }
    }
    
    public boolean isConsistent(V variable, HashMap<V, D> assignment) {
        for (Constraint constraint : this.constraints.get(variable)) {
            if (!constraint.isSatisfied(assignment)) {
                return false;
            }
        }
        return true;
    }
    
    public HashMap<V, D> backtrackingSearch(HashMap<V, D> assignment) {
        if (assignment.keySet().size() == this.variables.size()) {
            return assignment;
        }
        
        ArrayList<V> unassigned = this.variables.stream()
                .filter((variable) -> (!assignment.containsKey(variable)))
                .collect(Collectors.toCollection(ArrayList::new));
        
        V first = unassigned.get(0);
        for (D domainValue : this.domains.get(first)) {
            HashMap<V, D> localAssignment = new HashMap(assignment);
            localAssignment.put(first, domainValue);
            if (isConsistent(first, localAssignment)) {
                HashMap<V, D> solution = backtrackingSearch(localAssignment);
                if (solution != null) {
                    return solution;
                }
            }
            
        }
        
        return null;
    }

    public HashMap<V, ArrayList<Constraint<V, D>>> getConstraints() {
        return constraints;
    }
}
