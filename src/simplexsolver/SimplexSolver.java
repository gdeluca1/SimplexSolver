package simplexsolver;

import javax.swing.JFrame;

/**
 *
 * @author Gennaro
 */
public class SimplexSolver 
{
    private static JFrame _parent;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
//        SimplexUtilities.buildEquation("13X0 + X2 + 4X3 + X1 +*");
        SimplexFrame frame = new SimplexFrame();
        frame.setVisible(true);
    }
    
    /**
     * Returns the parent panel of the GUI.
     * @return 
     */
    public static JFrame getParent()
    {
        return _parent;
    }
}
