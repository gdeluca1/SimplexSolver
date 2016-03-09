package simplexsolver;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This is a JPanel containing several buttons for use by the user.
 * @author Gennaro
 */
public class ButtonPanel extends JPanel
{
    private final JButton _solveButton;
    private final JButton _addEqButton;
    private final JButton _removeEqButton;
    
    public ButtonPanel()
    {
        super();
        _solveButton = new JButton("Solve");
        _addEqButton = new JButton("+");
        _removeEqButton = new JButton("-");
        
        JPanel spacingPanel = new JPanel();
        spacingPanel.setBorder(BorderFactory.createEmptyBorder(0, 130, 0, 130));
        
        add(_solveButton);
        add(spacingPanel);
        add(_addEqButton);
        add(_removeEqButton);
    }
    
    public JButton getSolveButton()
    {
        return _solveButton;
    }
    
    public JButton getAddButton()
    {
        return _addEqButton;
    }
    
    public JButton getRemoveButton()
    {
        return _removeEqButton;
    }
}
