package simplexsolver;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This panel allows input of the objective of the linear program.
 * @author Gennaro
 */
public class ObjectivePanel extends JPanel
{
    private final JComboBox _target;
    private final String[] _potentialTargets =
    {
        "Max",
        "Min"
    };
    private final JTextField _equation;
    
    public ObjectivePanel()
    {
        super();
        _target = new JComboBox(_potentialTargets);
        
        JLabel zLabel = new JLabel("        Z = ");
        
        _equation = new JTextField();
        _equation.setColumns(30);
        
        add(_target);
        add(zLabel);
        add(_equation);
    }
    
    public SimplexUtilities.Objective getObjective()
    {
        if (String.valueOf(_target.getSelectedItem()).equals("Max"))
            return SimplexUtilities.Objective.MAX;
        else if (String.valueOf(_target.getSelectedItem()).equals("Min"))
            return SimplexUtilities.Objective.MIN;
        else
            throw new IllegalStateException("Only Max and Min should be selectable options.");
    }
    
    public String getEquation()
    {
        return _equation.getText();
    }
}
