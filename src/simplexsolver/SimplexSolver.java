package simplexsolver;

import javax.swing.JFrame;

/**
 *
 * @author Gennaro
 */
public class SimplexSolver 
{
    private static SimplexFrame _frame;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
//        SimplexUtilities.buildEquation("13X0 + X2 + 4X3 + X1 +*");
        _frame = new SimplexFrame();
        SimplexController.attachController(_frame);
        
        // For testing.
        int testCase = -1;
        if (args.length != 0)
        {
            System.out.println(args[0]);
            if (args[0].matches("^-?\\d+$"))
            {
                testCase = Integer.parseInt(args[0]);
            }
        }
        
        // Test Case 1:
        if (testCase == 1)
        {
            ConstraintPanel constraint = _frame.getConstraintPanels().peek();
            constraint.setValues("2X1 + X2 + 3X3", 2, 60);
            _frame.addConstraintPanel().setValues("3X1 + 3X2 + 5X3", 1, 120);
            _frame.getObjectivePanel().setValues("3X1 + 2X2 + 4X3", 1);
            _frame.getVariablesPanel().setValues(3);
        }

        // Test Case 2:
        else if (testCase == 2)
        {
            _frame.getVariablesPanel().setValues(3);
            _frame.getObjectivePanel().setValues("-X1 + X2 + 2X3", 0);
            ConstraintPanel constraint = _frame.getConstraintPanels().peek();
            constraint.setValues("X1 + 2X2 - X3", 0, 20);
            _frame.addConstraintPanel().setValues("-2X1 + 4X2 + 2X3", 0, 60);
            _frame.addConstraintPanel().setValues("2X1 + 3X2 + X3", 0, 50);
        }
        
        else if (testCase == 3)
        {
            _frame.getVariablesPanel().setValues(2);
            _frame.getObjectivePanel().setValues("15X1 + 20X2", 1);
            ConstraintPanel constraint = _frame.getConstraintPanels().peek();
            constraint.setValues("X1 + 2X2", 1, 10);
            _frame.addConstraintPanel().setValues("2X1 - 3X2", 0, 6);
            _frame.addConstraintPanel().setValues("X1 + X2", 1, 6);
        }
        
        else if (testCase == 4)
        {
            _frame.getVariablesPanel().setValues(2);
            _frame.getObjectivePanel().setValues("X1 + X2", 0);
            ConstraintPanel constraint = _frame.getConstraintPanels().peek();
            constraint.setValues("X1 + 2X2", 0, 8);
            _frame.addConstraintPanel().setValues("3X1 + 2X2", 0, 12);
            _frame.addConstraintPanel().setValues("X1 + 3X2", 1, 13);
        }
        
        else if (testCase == 5)
        {
            _frame.getVariablesPanel().setValues(7);
            _frame.getObjectivePanel().setValues("15X1 - 12X2 - 10X3 - 23X4 + 24X5 + 11X6 - 48X7", 0);
            ConstraintPanel constraint = _frame.getConstraintPanels().peek();
            constraint.setValues("4X1 - 3X2 - 2X3 - 6X4 + 8X5 + 3X6 - 9X7", 2, 184);
            _frame.addConstraintPanel().setValues("-2X1 + X2 + 4X3 + 4X4 + 2X5 - X6 + X7", 2, -106);
            _frame.addConstraintPanel().setValues("12X1 - 4X2 - 6X3 - 13X4 + 9X5 + 4X6 - 7X7", 2, 332);
        }
        
        _frame.setVisible(true);
    }
    
    /**
     * Returns the parent panel of the GUI.
     * @return 
     */
    public static JFrame getParent()
    {
        return _frame;
    }
}
