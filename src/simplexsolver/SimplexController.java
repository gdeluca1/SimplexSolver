package simplexsolver;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class acts as an interface between the SimplexFrame and the
 * SimplexSolver.
 * @author Gennaro
 */
public class SimplexController 
{    
    private SimplexController()
    {
        throw new UnsupportedOperationException("SimplexController should not be initialized.");
    }
    
    private static SimplexFrame _frame;
    
    /**
     * Attaches the controller to the input frame.
     * @param frame The GUI to control.
     */
    public static void attachController(SimplexFrame frame)
    {
        _frame = frame;
        attachEventHandlers();
    }
    
    private static void attachEventHandlers()
    {
        _frame.getButtonPanel().getSolveButton().addActionListener(e ->
        {
            // First, check that everything has been input.
            if (!_frame.getVariablesPanel().variableCountEntered())
            {
                GraphicUtilities.showErrorMessage("Please enter a number of decision variables.", "Error");
                return;
            }
            if (_frame.getObjectivePanel().getEquation().trim().equals(""))
            {
                GraphicUtilities.showErrorMessage("Please enter an objective function.", "Error");
                return;
            }
            for (ConstraintPanel panel : _frame.getConstraintPanels())
            {
                if (panel.getEquation().trim().equals(""))
                {
                    GraphicUtilities.showErrorMessage("Please enter an equation on the left hand side of your constraint.", "Error");
                    return;
                }
                if (!panel.rightHandSideEntered())
                {
                    GraphicUtilities.showErrorMessage("Please enter a constant on the right hand side of your constraint.", "Error");
                    return;
                }
            }
            
            // Now we can check the syntax of what has been entered.
            // First we have to add all the variables.
            SimplexUtilities.reset();
            for (int i = 1; i <= _frame.getVariablesPanel().getVariableCount(); i++)
            {
                SimplexUtilities.addNewVariable(new Variable("X" + i, i));
            }
            
            // Check the objective function.
            HashMap<Variable, Double> objectiveFunction = SimplexUtilities.buildEquation(_frame.getObjectivePanel().getEquation());
            if (objectiveFunction == null)
            {
                // There was something wrong (and an error message has already been shown). Stop running.
                return;
            }
            
            // Check all of the constraints.
            ArrayList<HashMap<Variable, Double>> constraints = new ArrayList<>();
            for (ConstraintPanel panel : _frame.getConstraintPanels())
            {
                HashMap<Variable, Double> constraint = SimplexUtilities.buildEquation(panel.getEquation());
                if (constraint == null)
                {
                    // Error in constraint. An error message has already been shown. Stop running.
                    return;
                }
                // Add the constraint to the list.
                constraints.add(constraint);
            }
            
            // If we reach this point, the syntax is all valid. We can start using the
            // Simplex algorithm now.
            
        });
        
        _frame.getButtonPanel().getAddButton().addActionListener(e ->
        {
            _frame.addConstraintPanel();
            System.out.println("Adding panel.");
        });
        
        _frame.getButtonPanel().getRemoveButton().addActionListener(e ->
        {
           // There must be at least one constraint at all times.
           if (_frame.getConstraintPanelCount() > 1)
           {
               _frame.removeConstraintPanel();
           }
        });
    }
}
