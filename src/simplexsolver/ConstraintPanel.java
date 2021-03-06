package simplexsolver;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Contains the elements required to input a single constraint.
 * @author Gennaro
 */
public class ConstraintPanel extends JPanel
{
    private final JTextField _leftHandSide;
    private final JComboBox _operator;
    private final String[] _potentialOperators = 
    {
        "≤",
        "≥",
        "="
    };
    private final JFormattedTextField _rightHandSide;
    
    public ConstraintPanel()
    {
        super();
        _leftHandSide = new JTextField();
        _leftHandSide.setColumns(30);
        _operator = new JComboBox(_potentialOperators);
        
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        decimalFormat.setGroupingUsed(false);
        _rightHandSide = new JFormattedTextField(decimalFormat);
        _rightHandSide.setColumns(5);
        
        add(_leftHandSide);
        add(_operator);
        add(_rightHandSide);
    }
    
    public String getEquation()
    {
        return _leftHandSide.getText();
    }
    
    public SimplexUtilities.Sign getSign()
    {
        if (String.valueOf(_operator.getSelectedItem()).equals("≥"))
            return SimplexUtilities.Sign.GREATER_THAN;
        else if (String.valueOf(_operator.getSelectedItem()).equals("≤"))
            return SimplexUtilities.Sign.LESS_THAN;
        else if (String.valueOf(_operator.getSelectedItem()).equals("="))
            return SimplexUtilities.Sign.EQUAL;
        else
            throw new IllegalStateException("Only ≥, ≤, and = should be selectable options.");
    }
    
    public boolean rightHandSideEntered()
    {
        return !_rightHandSide.getText().trim().equals("");
    }
    
    public double getRightHandSide()
    {
        return Double.parseDouble(_rightHandSide.getText());
    }
    
    /**
     * Only for testing!
     */
    public void setValues(String eq, int sign, int rhs)
    {
        _leftHandSide.setText(eq);
        _operator.setSelectedIndex(sign);
        _rightHandSide.setText(rhs + "");
    }
}
