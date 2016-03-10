package simplexsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Represents a tableau for use in the simplex algorithm.
 * @author Gennaro
 */
public class Tableau
{
    private double[][] _matrix;
    private TreeSet<Variable> _variables;
    private ObjectiveFunction _objective;
    private boolean _requiresTwoPhase = false;
    
    public Tableau(ObjectiveFunction objective, ArrayList<Constraint> constraints)
    {
        _objective = objective;
        // Add all the variables to the list, and sort by index number.
        // Variable index 1 will be index 0 in the matrix, and so on.
        _variables = new TreeSet<>((v1, v2) -> v1.getIndex() - v2.getIndex());
        HashMap<Variable, Double> objectiveEquation = objective.getEquation();
        objectiveEquation
                .entrySet()
                .stream()
                .forEach(kvp -> _variables.add(kvp.getKey()));
        
        // We also need some slack/surplus/artificial variables.
        // If _variables has 3 items, that means the next index is 4.
        int sVariableIndex = 1;
        int aVariableIndex = 1;
        int originalVariablesSize = _variables.size();
        for (Constraint constraint : constraints)
        {
            switch (constraint.getSign())
            {
                // +Si
                case LESS_THAN:
                    _variables.add(new Variable("S" + sVariableIndex, _variables.size() + 1));
                    sVariableIndex++;
                    break;
                    
                // -Si + Ai
                case GREATER_THAN:
                    _variables.add(new Variable("S" + sVariableIndex, _variables.size() + 1));
                    _variables.add(new Variable("A" + aVariableIndex, _variables.size() + 1));
                    sVariableIndex++;
                    aVariableIndex++;
                    break;
                    
                // +Ai
                case EQUAL:
                    _variables.add(new Variable("A" + aVariableIndex, _variables.size() + 1));
                    aVariableIndex++;
                    break;
            }
        }
        
        // There are artificial variables, so we will have to do two-phase.
        if (aVariableIndex > 1)
        {
            _requiresTwoPhase = true;
        }
        
        // Now we know how big to make the array
        // # of rows = 1 + # of constraints
        // # of columns = # of variables.
        _matrix = new double[1 + constraints.size()][_variables.size()];
        
        // Start filling in the tableau.
        // Row 0: objective function (only if not two-phase).
        if (!_requiresTwoPhase)
        {
            for (int i = 0; i < _variables.size(); i++)
            {
                // Objective function has no s or a variables.
                int currentIndex = i + 1;
                Optional<Entry<Variable, Double>> entry = objectiveEquation
                        .entrySet()
                        .stream()
                        .filter(kvp -> kvp.getKey().getIndex() == currentIndex)
                        .findFirst();

                // I am assuming that each variable only appears once. This
                // assumption should be held by the programmer, and cannot be broken
                // by the user.
                // Entry will be null for s and a variables.
                if (!entry.isPresent())
                    _matrix[0][i] = 0.0;
                else
                    _matrix[0][i] = -entry.get().getValue();
            }
        }
        
        // Track the S/A variables that have already been used.
        ArrayList<Integer> usedIndices = new ArrayList<>();
        ArrayList<Integer> artificalIndices = new ArrayList<>();
        // Rows 1 - n: Constraints
        for (int i = 0; i < constraints.size(); i++)
        {
            HashMap<Variable, Double> constraintEquation = constraints.get(i).getEquation();
            for (int j = 0; j < _variables.size(); j++)
            {
                if (j >= originalVariablesSize)
                {
                    // This is an S/A variable.
                    if (usedIndices.contains(j))
                    {
                        // This index has already been handled. Set it to 0 and continue.
                        _matrix[i + 1][j] = 0.0;
                        continue;
                    }
                    else
                    {
                        // This index belongs to this constraint.
                        switch (constraints.get(i).getSign())
                        {
                            // +Si
                            case LESS_THAN:
                                _matrix[i + 1][j] = 1;
                                usedIndices.add(j);
                                break;

                            // -Si + Ai
                            case GREATER_THAN:
                                _matrix[i + 1][j] = -1;
                                _matrix[i + 1][j + 1] = 1;
                                j++;
                                usedIndices.add(j - 1);
                                usedIndices.add(j);
                                artificalIndices.add(j);
                                break;

                            // +Ai
                            case EQUAL:
                                _matrix[i + 1][j] = 1;
                                usedIndices.add(j);
                                artificalIndices.add(j);
                                break;
                        }
                        break;
                    }
                }
                int currentIndex = j + 1;
                Optional<Entry<Variable, Double>> entry = constraintEquation
                        .entrySet()
                        .stream()
                        .filter(kvp -> kvp.getKey().getIndex() == currentIndex)
                        .findFirst();
                
                if (entry.isPresent())
                {
                    _matrix[i + 1][j] = entry.get().getValue();
                }
                else
                {
                    throw new IllegalStateException("The case where this is a non-normal variable should have been handled.");
                }
            }
        }
        
        // If this is a two-phase problem, our first equation will be different.
        if (_requiresTwoPhase)
        {
            for (int i = 0; i < _variables.size(); i++)
            {
                _matrix[0][i] = 0.0;
                if (artificalIndices.contains(i))
                    _matrix[0][i] = -1.0;
                
                // Each element in this row is the sum of every other element in that column.
                for (int j = 1; j <= constraints.size(); j++)
                {
                    _matrix[0][i] += _matrix[j][i];
                }
            }
        }
        
        System.out.println(stringifyTableau(_variables, _matrix));
    }
    
    private String stringifyTableau(TreeSet<Variable> variables, double[][] matrix)
    {
        StringBuilder toReturn = new StringBuilder();
        variables.stream().forEach((variable) -> 
        {
            toReturn.append(variable.getName()).append("\t");
        });
        for (double[] matrix1 : matrix) 
        {
            toReturn.append("\n");
            for (double item : matrix1)
            {
                toReturn.append(item).append("\t");
            }
        }
        return toReturn.toString();
    }
}
