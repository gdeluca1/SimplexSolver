package simplexsolver;

import java.util.HashMap;

/**
 * Represents a single constraint in a linear program.
 * @author Gennaro
 */
public class Constraint 
{
    private HashMap<Variable, Integer> _equation;
    private SimplexUtilities.Sign _sign;
    private int _rhs;
    
    /**
     * Define a constraint.
     * @param equation The constraint's equation.
     * @param sign The sign of the equation.
     * @param rhs The constant on the right hand side of the equation.
     */
    public Constraint(HashMap<Variable, Integer> equation, SimplexUtilities.Sign sign, int rhs)
    {
        _equation = equation;
        _sign = sign;
        _rhs = rhs;
    }
    
    public HashMap<Variable, Integer> getEquation()
    {
        return _equation;
    }
    
    public SimplexUtilities.Sign getSign()
    {
        return _sign;
    }
    
    public int getRightHandSide()
    {
        return _rhs;
    }
}
