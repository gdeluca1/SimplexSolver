package simplexsolver;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * This class is used for displaying messages to the user.
 * @author Gennaro
 */
public class GraphicUtilities 
{
    private GraphicUtilities()
    {
        throw new UnsupportedOperationException("GraphicUtilities should not be instantiated.");
    }
    
    public static void showErrorMessage(String error, String errorTitle)
    {
        JOptionPane.showMessageDialog(SimplexSolver.getParent(), error, errorTitle, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showMessage(String message, String messageTitle)
    {
        JTextArea messageArea = new JTextArea(message);
        JOptionPane.showMessageDialog(SimplexSolver.getParent(), messageArea, messageTitle, JOptionPane.INFORMATION_MESSAGE);
    }
}
