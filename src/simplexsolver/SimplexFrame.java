package simplexsolver;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
            "Decision variables are named X0, X1, ..., XN-1 where N is your input number.</html>";
    public SimplexFrame()
    {
        super();
        setSize(700, 700);
        setTitle("Simplex Solver");
        
        JPanel container = new JPanel();
        JScrollPane scrollPane = new JScrollPane(container);
        add(scrollPane);
//        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//        setLayout(new GridLayout(0, 1));
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        
        JPanel fillerPanel = new JPanel();
        c.gridx = 0;
        // When this is sufficiently large, the user will never notice the break. That would require
        // a monitor that can fit gridy rows in one screen + some extra space.
        c.gridy = 1024;
        c.weighty = 1;
        container.add(fillerPanel, c);
        
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        
        JLabel decisionVarInfoLabel = new JLabel(_decisionVariableInfo);
        decisionVarInfoLabel.setFont(decisionVarInfoLabel.getFont().deriveFont(12.0f));
        container.add(decisionVarInfoLabel, c);
        
//        c.gridy = 1;
        c.gridy = GridBagConstraints.RELATIVE;
        container.add(new ConstraintPanel(), c);
        
//        c.gridy = 2;
        container.add(new ConstraintPanel(), c);
        
//        c.gridy = 3;
        container.add(new ConstraintPanel(), c);
        
//        c.gridy = 4;
        c.anchor = GridBagConstraints.PAGE_END;
        container.add(new JLabel("Hello"), c);
        
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        
        for (int i = 0; i < 20; i++)
        {
//            c.gridy = 5 + i;
            container.add(new ConstraintPanel(), c);
        }
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
