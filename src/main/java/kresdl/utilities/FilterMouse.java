package kresdl.utilities;

import java.awt.Container;
import java.awt.Point;
import java.util.Arrays;

import kresdl.geometry.Vec2;

/**
 * Mouse with filter for smooth movement.
 */
public class FilterMouse extends AbstractMouse {

    private int q = 0;
    private final int[] dx = new int[5],
            dy = new int[5];
    private Vec2 movement = new Vec2();

    /**
     * Constructs FilterMouse and registers it on given container.
     *
     * @param c container
     */
    public FilterMouse(Container c) {
        super(c);
    }

    @Override
    synchronized boolean test(Point d) {
        Vec2 t = avg(d);
        if (t.x == 0 && t.y == 0) {
            return false;
        }
        movement = t;
        return true;
    }

    /**
     * Returns the mouse movement in double-precision since last call to this
     * method.
     *
     * @return mouse movement
     */
    public synchronized Vec2 getMovement() {
        old = current;
        return movement;
    }

    private Vec2 avg(Point p) {
        dx[q] = p.x;
        dy[q++] = p.y;
        q %= 5;
        return new Vec2(avg(dx), avg(dy));
    }

    private double avg(int[] delta) {
        return Arrays.stream(delta).average().getAsDouble();
    }
}
