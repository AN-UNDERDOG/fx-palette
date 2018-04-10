package kresdl.utilities;

import java.awt.event.ItemEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * Radio button group
 */
@SuppressWarnings("serial")
public class RBGroup extends ButtonGroup {

    private String actionCommand;

    /**
     * Construct empty radio button group.
     */
    public RBGroup() {
        super();
    }

    /**
     * Add radio button
     *
     * @param r radio button
     */
    public void add(JRadioButton r) {
        super.add(r);
        r.setActionCommand(r.getText());
        r.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                actionCommand = r.getActionCommand();
            }
        });
    }

    /**
     * Get action command.
     *
     * @return the action command for the selected button.
     */
    public String getActionCommand() {
        return actionCommand;
    }
}
