package simplexsolver;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides utilities needed for creating and solving
 * a linear program.
 * @author Gennaro
 */
public class SimplexUtilities 
{
    private SimplexUtilities() 
    { 
        throw new UnsupportedOperationException("SimplexUtilities should not be instantiated.");
    }
    
    /**
     * Represents the three possible signs in a linear program equation.
     * LESS_THAN and GREATER_THAN both implicitly assume "or equal to."
     */
    public enum Sign
    {
        EQUAL, LESS_THAN, GREATER_THAN
    }
    
    /**
     * Represents the two possible objectives to be reached in a linear
     * program (either min or max the objective function).
     */
    public enum Objective
    {
        MIN, MAX
    }
    
    private static TreeSet<Variable> _variables = null;
    
    /**
     * Adds a new decision variable to the set of variables.
     * @param variable The variable to add.
     */
    public static void addNewVariable(Variable variable)
    {
        if (_variables == null)
        {
            // This set should sort by variable index to guarantee order in all other objects.
            _variables = new TreeSet<>((Variable v1, Variable v2) ->
            {
                return v1.getIndex() - v2.getIndex();
            });
        }
        
        _variables.add(variable);
    }
    
    /**
     * Resets the variables for a new run of the simplex algorithm.
     */
    public static void reset()
    {
        _variables = null;
    }
    
    /**
     * Checks the passed in equation string for correct syntax. Returns
     * null if no error, or an error message if something is incorrect.
     * @param equationString The equation to parse.
     * @return An error message, or null if no errors.
     */
    private static String checkEquationSyntax(String equationString)
    {
        // Format: c1X1 + c2X2 + ... cnXn
        String pattern = "(\\d*[A-Z]+\\d*\\s[+-]\\s)*(\\d*[A-Z]+\\d*)";
        equationString = equationString.toUpperCase().trim();
        int syntaxError = findSyntaxError(Pattern.compile(pattern), equationString);
        if (syntaxError == -1)
            return null;
        else if (syntaxError == equationString.length())
            return "Your equation is incomplete: " + equationString;
        else
            return "Invalid character <" + equationString.charAt(syntaxError) + "> at index " + 
                    syntaxError + " in equation: " + equationString;
    }
    
    /**
     * Returns the first index of a syntax error (or -1 if there is no syntax error).
     * @return 
     */
    private static int findSyntaxError(Pattern pattern, String string)
    {
        // Step through the string until invalid syntax is found.
        for (int i = 0; i <= string.length(); i++) 
        {
            Matcher m = pattern.matcher(string.substring(0, i));
            if (!m.matches() && !m.hitEnd()) 
            {
                return i - 1;
            }
        }
        // If no syntax error was found, either it is a match (this case)...
        if (pattern.matcher(string).matches()) 
        {
            return -1;
        }
        // ... or the pattern was not fully matched (the equation is missing characters).
        else 
        {
            return string.length();
        }
    }
    
    public static HashMap<Variable, Integer> buildEquation(String equationString)
    {
        HashMap<Variable, Integer> toReturn = new HashMap<>();
        String syntaxError = checkEquationSyntax(equationString);
        if (syntaxError != null)
        {
            GraphicUtilities.showErrorMessage(syntaxError, "Syntax error");
            return null;
        }
        
        return toReturn;
    }
}
