package kresdl.utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FocusTraversalPolicy implementation.
 */
public class FocusCyclePolicy extends FocusTraversalPolicy {

    private List<Component> c = new ArrayList<>();

    /**
     * Construct policy from component array.
     * @param c component array
     */
    public FocusCyclePolicy(Component[] c) {
        this.c = Arrays.asList(c);
    }

    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {
        int i = c.indexOf(aComponent) + 1;
        i = (i < c.size() ? i : 0);
        return c.get(i);
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        int i = c.indexOf(aComponent) - 1;
        i = (i < 0 ? c.size() - 1 : i);
        return c.get(i);
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        return c.get(0);
    }

    @Override
    public Component getFirstComponent(Container aContainer) {
        return c.get(0);
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        return c.get(c.size() - 1);
    }
}
