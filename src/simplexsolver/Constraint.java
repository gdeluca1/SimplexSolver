package simplexsolver;

import java.util.HashMap;

/**
 * Represents a single constraint in a linear program.
 * @author Gennaro
 */
public class Constraint 
{
    private final HashMap<Variable, Double> _equation;
    private final SimplexUtilities.Sign _sign;
    private final double _rhs;
    
    /**
     * Define a constraint.
     * @param equation The constraint's equation.
     * @param sign The sign of the equation.
     * @param rhs The constant on the right hand side of the equation.
     */
    public Constraint(HashMap<Variable, Double> equation, SimplexUtilities.Sign sign, double rhs)
    {
        _equation = equation;
        _sign = sign;
        _rhs = rhs;
    }
    
    public HashMap<Variable, Double> getEquation()
    {
        return _equation;
    }
    
    public SimplexUtilities.Sign getSign()
    {
        return _sign;
    }
    
    public double getRightHandSide()
    {
        return _rhs;
    }
}
