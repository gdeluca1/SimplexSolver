package simplexsolver;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BoxLayout;
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
        "=",
        "≤",
        "≥"
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
        
//        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        add(_leftHandSide);
        add(_operator);
        add(_rightHandSide);
    }
}
