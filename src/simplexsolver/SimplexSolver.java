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
