package simplexsolver;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
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
    private ArrayList<Constraint> _constraints;
    private boolean _requiresTwoPhase = false;
    private LinkedList<Integer> _basicVariables;
    private boolean _wasMinimize;
    private ArrayList<Integer> _artificalIndices;
    
    public Tableau(ObjectiveFunction objective, ArrayList<Constraint> constraints)
    {
        _objective = objective;
        _constraints = constraints;
        _wasMinimize = !_objective.isMaximize();
        if (!_objective.isMaximize())
            _objective.convertToMaximize();
        
        _basicVariables = new LinkedList<>();
        
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
        // # of columns = # of variables + 1 for RHS.
        _matrix = new double[1 + constraints.size()][_variables.size() + 1];
        
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
            
            // The RHS of the objective function starts at 0.
            _matrix[0][_matrix[0].length - 1] = 0;
        }
        
        // Track the S/A variables that have already been used.
        ArrayList<Integer> usedIndices = new ArrayList<>();
        _artificalIndices = new ArrayList<>();
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
                                // Si is a basic variable.
                                _basicVariables.addLast(j);
                                break;

                            // -Si + Ai
                            case GREATER_THAN:
                                _matrix[i + 1][j] = -1;
                                _matrix[i + 1][j + 1] = 1;
                                j++;
                                usedIndices.add(j - 1);
                                usedIndices.add(j);
                                _artificalIndices.add(j);
                                // Ai is a basic variable.
                                _basicVariables.addLast(j);
                                break;

                            // +Ai
                            case EQUAL:
                                _matrix[i + 1][j] = 1;
                                usedIndices.add(j);
                                _artificalIndices.add(j);
                                // Ai is a basic variable.
                                _basicVariables.addLast(j);
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
            
            // Add in the RHS.
            _matrix[i + 1][_matrix[i + 1].length - 1] = _constraints.get(i).getRightHandSide();
        }
        
        // If this is a two-phase problem, our first equation will be different.
        if (_requiresTwoPhase)
        {
            for (int i = 0; i <= _variables.size(); i++)
            {
                _matrix[0][i] = 0.0;
                if (_artificalIndices.contains(i))
                    _matrix[0][i] = -1.0;
                
                // Each element in this row is the sum of every other element in that column.
                for (int j = 1; j <= constraints.size(); j++)
                {
                    _matrix[0][i] += _matrix[j][i];
                }
            }
        }
        
        System.out.println(stringifyTableau(_variables, _matrix, _requiresTwoPhase ? 'W' : 'Z'));
    }
    
    public String solve()
    {
        if (_requiresTwoPhase)
            if (!solveFirstPhase())
                return null;
        
        if (!solveWithSimplex('Z'))
            return null;
        
        if (_wasMinimize)
        {
            _matrix[0][_matrix[0].length - 1] *= -1;
        }
        return stringifyTableau(_variables, _matrix, 'Z');
    }
    
    private boolean solveFirstPhase()
    {
        // The objective in the first phase is to minimze W.
        // We have a method to solve maximize problems, so convert it
        // to a maximize W problem.
        for (int i = 0; i < _matrix[0].length; i++)
        {
            _matrix[0][i] = -_matrix[0][i];
        }
        System.out.println(stringifyTableau(_variables, _matrix, 'W'));
        if (!solveWithSimplex('W'))
            return false;
        
        // Now we need to put the original equation back in for phase 2.
        for (int i = 0; i < _variables.size(); i++)
        {
            // Objective function has no s or a variables.
            int currentIndex = i + 1;
            Optional<Entry<Variable, Double>> entry = _objective.getEquation()
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
        
        System.out.println(stringifyTableau(_variables, _matrix, 'Z'));
        
        // Now we need to remove basic variables from the objective function.
        ListIterator<Integer> iter = _basicVariables.listIterator();
        int index = 0;
        while (iter.hasNext())
        {
            int bv = iter.next();
            // bv = index of basic variable (column number).
            // index = row which contains that variable set to 1.
            double coefficient = _matrix[0][bv];
            for (int i = 0; i < _matrix[0].length; i++)
            {
                _matrix[0][i] -= (coefficient * _matrix[index + 1][i]);
            }
            
            index++;
        }
        
        // Finally, we have to remove the artificial variables.
        removeArtificialVariables();
        
        System.out.println(stringifyTableau(_variables, _matrix, 'Z'));
        
        return true;
    }
    
    private void removeArtificialVariables()
    {
        // We have to adjust the indices of the basic variables as we perform this operator.
        double[][] newMatrix = new double[_matrix.length][_matrix[0].length - _artificalIndices.size()];
        
        int actualColumn = newMatrix[0].length;
        // First iterate over the columns.
        for (int j = _matrix[0].length - 1; j >= 0; j--)
        {
            if (!_artificalIndices.contains(j))
            {
                actualColumn--;
            }
            else
            {
                // This column will be removed. Update the basic variable list.
                for (int k = 0; k < _basicVariables.size(); k++)
                {
                    if (_basicVariables.get(k) == j)
                    {
                        // Artifical variables should never be basic variables at this point.
                        GraphicUtilities.showErrorMessage("Error: An artifical variable is a " +
                                "basic variable after the first phase completed.", "ERROR");
                    }
                    else if (_basicVariables.get(k) > j)
                    {
                        // Decrease the index to make up for the loss of the artifical variable.
                        // We are iterating the list backwards, so deducting 1 should not cause an issue.
                        _basicVariables.set(k, _basicVariables.get(k) - 1);
                    }
                    
                    // We also need to remove the artifical variable from the variables list.
                    // Remember that variable indices start at 1.
                    int indexToRemove = j + 1;
                    _variables.removeIf(var -> var.getIndex() == indexToRemove);
                }
                continue;
            }
            for (int i = 0; i < _matrix.length; i++)
            {
                newMatrix[i][actualColumn] = _matrix[i][j];
            }
        }
        
        _matrix = newMatrix;
    }
    
    /**
     * This method assumes that the objective function is the first row
     * in the tableau and that it is a maximize.
     */
    private boolean solveWithSimplex(char objectiveVariable)
    {
        // Run forever (until a solution is found).
        for (;;)
        {
            // First we have to find the entering variable (most negative coefficient).
            double mostNegative = _matrix[0][0];
            int mostNegativeIndex = 0;
            for (int i = 1; i < _matrix[0].length - 1; i++)
            {
                if (_matrix[0][i] < mostNegative)
                {
                    mostNegative = _matrix[0][i];
                    mostNegativeIndex = i;
                }
            }
            
            // If there are no negative numbers, we are done.
            // Due to rounding errors, we must include an approximation here.
            if (mostNegative >= -1E-10)
            {
                break;
            }
            
            // Now we have to find the leaving variable (samllest MRT).
            double[] mrt = new double[_matrix.length - 1];
            
            // We don't need to perform the mrt for the objective function.
            for (int i = 1; i < _matrix.length; i++)
            {
                if (_matrix[i][mostNegativeIndex] == 0 || _matrix[i][_matrix[i].length - 1] / _matrix[i][mostNegativeIndex] < 0)
                    mrt[i - 1] = Double.POSITIVE_INFINITY;
                else
                    mrt[i - 1] = _matrix[i][_matrix[i].length - 1] / _matrix[i][mostNegativeIndex];
            }
            
            // Find the smallest mrt (if it is a tie, we will grab the first one we see).
            double smallestMRT = mrt[0];
            int smallestMRTIndex = 0;
            for (int i = 1; i < mrt.length; i++)
            {
                if (mrt[i] < smallestMRT)
                {
                    smallestMRT = mrt[i];
                    smallestMRTIndex = i;
                }
            }
            
            if (smallestMRT == Double.POSITIVE_INFINITY)
            {
                GraphicUtilities.showErrorMessage("The LP is unbounded and cannot be solved.", "Solve Error");
                return false;
            }
            
            _basicVariables.set(smallestMRTIndex, mostNegativeIndex);
            
            // This index will be one less than expected since we ignored the
            // objective function. Increment it to fix that issue.
            smallestMRTIndex++;
            
            // Now that we have our entering and leaving variables, we need to
            // create a new matrix for our tableau.
            double[][] newMatrix = new double[_matrix.length][_matrix[0].length];
            
            // First we'll update the pivot row (divide by the coefficient of the entering variable).
            for (int i = 0; i < _matrix[smallestMRTIndex].length; i++)
            {
                newMatrix[smallestMRTIndex][i] = (_matrix[smallestMRTIndex][i] / _matrix[smallestMRTIndex][mostNegativeIndex]);
            }
            
            // Now we have to update all the other rows (make the entering variable value 0).
            for (int i = 0; i < _matrix.length; i++)
            {
                // This row has already been handled, skip it.
                if (i == smallestMRTIndex)
                    continue;
                
                double coefficient = _matrix[i][mostNegativeIndex];
                for (int j = 0; j < _matrix[i].length; j++)
                {
                    newMatrix[i][j] = _matrix[i][j] - coefficient * newMatrix[smallestMRTIndex][j];
                }
            }
            
            _matrix = newMatrix;
            System.out.println(stringifyTableau(_variables, _matrix, objectiveVariable));
        }
        
        return true;
    }
    
    private String stringifyTableau(TreeSet<Variable> variables, double[][] matrix, char objectiveVariable)
    {
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("BV\tEQ\t").append(objectiveVariable).append("\t");
        variables.stream().forEach((variable) -> 
        {
            toReturn.append(variable.getName()).append("\t");
        });
        toReturn.append("RHS");
        
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        int basicVariableCount = -1;
        
        for (int i = 0; i < matrix.length; i++) 
        {
            toReturn.append("\n");
            if (basicVariableCount == -1)
                toReturn.append(objectiveVariable).append("\t");
            else
            {
                // Get the basic variable.
                int basicVariableIndex = _basicVariables.get(basicVariableCount);
                Iterator<Variable> iter = _variables.iterator();
                for (int j = 0; j < basicVariableIndex; j++)
                {
                    iter.next();
                }
                toReturn.append(iter.next().getName()).append("\t");
            }
            toReturn.append(basicVariableCount + 1).append("\t");
            if (basicVariableCount == -1)
            {
                toReturn.append("1\t");
            }
            else
            {
                toReturn.append("0\t");
            }
            basicVariableCount++;
            for (int j = 0; j < matrix[i].length; j++)
            {
                toReturn.append(df.format(matrix[i][j])).append("\t");
            }
        }
        return toReturn.toString();
    }
}
