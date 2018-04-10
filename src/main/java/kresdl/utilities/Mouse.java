package kresdl.utilities;

import java.awt.Component;
import java.awt.Point;

/**
 * Mouse
 */
public class Mouse extends AbstractMouse {

    private Point movement = new Point();

    /**
     * Construct Mouse and registers it on given component.
     *
     * @param c component
     */
    public Mouse(Component c) {
        super(c);
    }

    @Override
    synchronized boolean test(Point d) {
        if (d.x == 0 && d.y == 0) {
            return false;
        }
        movement = d;
        return true;
    }

    /**
     * Returns the mouse movement since last call to this method.
     *
     * @return mouse movement
     */
    public synchronized Point getMovement() {
        old = current;
        return movement;
    }
}
