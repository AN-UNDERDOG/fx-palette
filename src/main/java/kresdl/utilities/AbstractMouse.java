package kresdl.utilities;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;

public abstract class AbstractMouse {

    Point current, old;
    private long timeStamp;
    private final Component c;

    AbstractMouse(Component c) {
        this.c = c;
    }

    /**
     * Returns absolute mouse position.
     *
     * @return absolute mouse position
     */
    public Point getAbs() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    synchronized boolean hasMoved() {
        current = getAbs();
        Point d = Misc.pointSub(current, old);
        return test(d);
    }

    abstract boolean test(Point d);

    /**
     * On mouse click.
     *
     * @param button button number, 0-3
     * @param f callback
     * @return mouse
     */
    public AbstractMouse onClick(int button, Consumer<MouseEvent> f) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 + button) {
                    timeStamp = System.currentTimeMillis();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 + button) {
                    if (System.currentTimeMillis() - timeStamp < 100) {
                        f.accept(e);
                    }
                }
            }
        });
        return this;
    }

    /**
     * On mouse press.
     *
     * @param button button number, 0-3
     * @param f callback
     * @return mouse
     */
    public AbstractMouse onPress(int button, Consumer<MouseEvent> f) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 + button) {
                    f.accept(e);
                }
            }
        });
        return this;
    }

    /**
     * On mouse release.
     *
     * @param button button number, 0-3
     * @param f callback
     * @return mouse
     */
    public AbstractMouse onRelease(int button, Consumer<MouseEvent> f) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 + button) {
                    f.accept(e);
                }
            }
        });
        return this;
    }

    /**
     * On mouse move.
     *
     * @param f callback
     * @return mouse
     */
    public AbstractMouse onMove(Consumer<MouseEvent> f) {
        c.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                old = getAbs();
            }
        });

        c.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (hasMoved()) {
                    f.accept(e);
                }
            }
        });
        return this;
    }

    /**
     * On mouse drag.
     *
     * @param button button number, 0-3
     * @param f callback
     * @return mouse
     */
    public AbstractMouse onDrag(int button, Consumer<MouseEvent> f) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 + button) {
                    old = getAbs();
                }
            }
        });

        c.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int mask;
                switch (button) {
                    case 0:
                        mask = 0x400;
                        break;
                    case 1:
                        mask = 0x800;
                        break;
                    default:
                        mask = 0x1000;
                }

                if (e.getModifiersEx() == mask) {
                    if (hasMoved()) {
                        f.accept(e);
                    }
                }
            }
        });
        return this;
    }
}
