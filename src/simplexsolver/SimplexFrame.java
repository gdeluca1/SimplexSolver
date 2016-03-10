package simplexsolver;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * The main JFrame window for the Simplex Solver.
 * @author Gennaro
 */
public class SimplexFrame extends JFrame
{
    private final String _decisionVariableInfo =
            "<html>Please input the number of decision variables you would like to use.<br>" +
            "Decision variables are named X1, X2, ..., XN where N is your input number.</html>";
    private final String _objectiveFunctionInfo =
            "<html>Input your objective function. Example: 3X1 + 4X2 - 8X3</html>";
    private final String _constraintsInfo =
            "<html>Now you can enter any number of constraints. The left hand side is in the same<br>" +
            "format as the objective function (e.g. 3X1 + 4X2 - 8X3). Then you must select an<br>" +
            "operator and type in a constant in the final box. All variables must be on the left<br>" +
            "hand side and all constants must be on the right hand side.<br>" +
            "Finally, you can use the Solve button to solve the equation, or the<br>" +
            "+ and - buttons to add or remove constraints.</html>";
    
    private final VariablesPanel _variablesPanel;
    private final ObjectivePanel _objectivePanel;
    private final ButtonPanel _buttonPanel;
    private final Stack<ConstraintPanel> _constraintPanels;
    
    private final GridBagConstraints c;
    private final JPanel container;
    
    public SimplexFrame()
    {
        super();
        setSize(700, 700);
        setTitle("Simplex Solver");
        
        container = new JPanel();
        JScrollPane scrollPane = new JScrollPane(container);
        add(scrollPane);

        container.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        
        JPanel fillerPanel = new JPanel();
        c.gridx = 0;
        // When this is sufficiently large, the user will never notice the break. That would require
        // a monitor that can fit gridy rows in one screen + some extra space.
        c.gridy = 1024;
        c.weighty = 1;
        container.add(fillerPanel, c);
        
//        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        
        // Wrap the label in a panel to maintain identical spacing.
        JPanel infoPanel = new JPanel();
        JLabel infoLabel = new JLabel(_decisionVariableInfo);
        infoLabel.setFont(infoLabel.getFont().deriveFont(12.0f));
        infoPanel.add(infoLabel);
        container.add(infoPanel, c);
        
        c.gridy = GridBagConstraints.RELATIVE;
        _variablesPanel = new VariablesPanel();
        container.add(_variablesPanel, c);
        
        infoPanel = new JPanel();
        infoLabel = new JLabel();
        infoLabel.setText(_objectiveFunctionInfo);
        infoPanel.add(infoLabel);
        container.add(infoPanel, c);
        
        _objectivePanel = new ObjectivePanel();
        container.add(_objectivePanel, c);
        
        infoPanel = new JPanel();
        infoLabel = new JLabel();
        infoLabel.setText(_constraintsInfo);
        infoPanel.add(infoLabel);
        container.add(infoPanel, c);
        
        _buttonPanel = new ButtonPanel();
        container.add(_buttonPanel, c);
        
        _constraintPanels = new Stack<>();
        _constraintPanels.push(new ConstraintPanel());
        container.add(_constraintPanels.peek(), c);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    /**
     * Adds a new constraint panel to the frame.
     * @return The constraint panel that was added.
     */
    public ConstraintPanel addConstraintPanel()
    {
        ConstraintPanel toAdd = new ConstraintPanel();
        _constraintPanels.push(toAdd);
        container.add(toAdd, c);
        container.revalidate();
        container.repaint();
        return toAdd;
    }
    
    /**
     * Removes the last added constraint panel. Will throw an exception
     * if there are no constraint panels to remove.
     * @return The constraint panel that was removed.
     */
    public ConstraintPanel removeConstraintPanel()
    {
        ConstraintPanel toRemove = _constraintPanels.pop();
        container.remove(toRemove);
        container.revalidate();
        container.repaint();
        return toRemove;
    }
    
    public int getConstraintPanelCount()
    {
        return _constraintPanels.size();
    }
    
    public Stack<ConstraintPanel> getConstraintPanels()
    {
        return _constraintPanels;
    }
    
    public ButtonPanel getButtonPanel()
    {
        return _buttonPanel;
    }
    
    public VariablesPanel getVariablesPanel()
    {
        return _variablesPanel;
    }
    
    public ObjectivePanel getObjectivePanel()
    {
        return _objectivePanel;
    }
}
