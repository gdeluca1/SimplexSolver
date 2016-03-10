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
        String pattern = "-?(\\d*[A-Z]+\\d+\\s?[+-]\\s?)*(\\d*[A-Z]+\\d+)";
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
        
        _variables.stream().forEach(v -> 
        {
            toReturn.put(v, 0);
        });
        
        // Since the syntax check was successful, we can remove all the whitespaces
        // to make parsing the string easier.
        equationString = equationString.replace(" ", "");
        
        // Now we have to step through the string and assign the correct integers
        // to each variable.
        for (int i = 0; i < equationString.length(); i++)
        {
            // First we need a number.
            int number = 0;
            boolean isNegative;
            char c = equationString.charAt(i);
            StringBuilder variableName = new StringBuilder();
            
            // If this is the first iteration, the first character may be a negative sign.
            if (i == 0)
            {
                isNegative = (c == '-');
                // If this is the first iteration, and the first char is a -, we need
                // to eat that char to reach the first number.
                if (isNegative)
                {
                    i++;
                    c = equationString.charAt(i);
                }
            }
            else
            {
                // If this is a different iteration, we will have to find a sign.
                // Check the sign.
                isNegative = (c == '-');
                i++;
                c = equationString.charAt(i);
            }
            
            // Since the regex was successful, there is guaranteed to be a variable
            // after every constant.
            while (isDigit(c))
            {
                // First, we have to make room for the new number in the one's place.
                number *= 10;
                number += Character.getNumericValue(c);
                i++;
                c = equationString.charAt(i);
            }
            
            // If the number remained 0, that means there was no number in front of the variable.
            // This means the coefficient of the variable is 1.
            if (number == 0)
                number = 1;
            
            if (isNegative)
                number *= -1;
            
            // Now we need the variable name. In the current implementation,
            // we will assume that all variables are named XN where N is an integer.
            do
            {
                // The first char is 'X'.
                // Build the variable number.
                variableName.append(c);
                i++;
                
                // If we reach the end of the string, i will extend one char too far,
                // in which case we should just exit the loop.
                if (i  == equationString.length())
                    break;
                c = equationString.charAt(i);
            } while (isDigit(c));
            
            // When we exit the loop, we want to decrement since the for loop
            // will automatically re-increment i.
            i--;
            
//            System.out.println("Constant: " + number + " Variable name: " + variableName.toString());
            
            Object[] matches = toReturn
                .keySet()
                .stream()
                .filter(key -> key.getName().equals(variableName.toString()))
                .toArray();
            
            if (matches.length > 1)
                throw new IllegalStateException("There are multiple variables with the same name.");
            else if (matches.length == 0)
            {
                GraphicUtilities.showErrorMessage(
                        "You attempted to use variable <" + variableName.toString() + "> but it is not a defined variable.", 
                        "Undeclared Variable");
                return null;
            }
            
            // If neither of the above error cases occurred, we have found the variable
            // corresponding to the coefficient we read in.
            toReturn.replace((Variable)matches[0], number);
        }
        
        
        return toReturn;
    }
    
    private static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }
}
