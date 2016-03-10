package simplexsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the objective function in a linear program.
 * @author Gennaro
 */
public class ObjectiveFunction 
{
    private HashMap<Variable, Integer> _equation;
    private SimplexUtilities.Objective _objective;
    
    public ObjectiveFunction(HashMap<Variable, Integer> equation, SimplexUtilities.Objective objective)
    {
        _equation = equation;
        _objective = objective;
    }
    
    public boolean isMaximize()
    {
        return _objective == SimplexUtilities.Objective.MAX;
    }
    
    public void convertToMaximize()
    {
        if (isMaximize())
            throw new IllegalStateException("Cannot convert the objective function to maximize if it is already a maximization problem.");
        
        // All we have to do is flip all the signs.
        _equation = (HashMap<Variable, Integer>)_equation
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, 
                        kvp -> kvp.getValue() * -1));
        
        _objective = SimplexUtilities.Objective.MAX;
    }
}
