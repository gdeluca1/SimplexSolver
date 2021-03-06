package simplexsolver;

/**
 * Represents a single decision variable in a linear program.
 * @author Gennaro
 */
public class Variable 
{
    private final String _name;
    private String _alias;
    private final int _index;
    
    /**
     * Defines a new decision variable with the specified name.
     * @param name The decision variable's name.
     * @param index The index of this variable (X1 is index 1, X4 is index 4, etc.).
     */
    public Variable(String name, int index)
    {
        _name = name;
        _index = index;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public String getAlias()
    {
        return _alias;
    }
    
    public int getIndex()
    {
        return _index;
    }
    
    public void setAlias(String alias)
    {
        _alias = alias;
    }

    @Override
    public String toString() 
    {
        return getName() + "@" + getIndex();
    }
}
