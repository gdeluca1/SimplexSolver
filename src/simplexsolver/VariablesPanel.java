package simplexsolver;

import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is a JPanel containing the controls required to set
 * the variables for the linear program.
 * @author Gennaro
 */
public class VariablesPanel extends JPanel
{
    private final JFormattedTextField _variableCount;
    
    public VariablesPanel()
    {
        super();
        NumberFormat format = NumberFormat.getIntegerInstance(Locale.getDefault());
        format.setMaximumFractionDigits(0);
        _variableCount = new JFormattedTextField(format);
        _variableCount.setColumns(5);
        
        JLabel descriptionLabel = new JLabel("Number of Decision Variables: ");
        add(descriptionLabel);
        add(_variableCount);
    }
    
    public boolean variableCountEntered()
    {
        return !_variableCount.getText().trim().equals("");
    }
    
    public int getVariableCount()
    {
        return Integer.parseInt(_variableCount.getText().replace(",", ""));
    }
}
