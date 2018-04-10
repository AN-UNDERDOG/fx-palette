/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kresdl.fxutilities;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import kresdl.geometry.Vec2;

/**
 *
 * @author Peter
 */
public class Mouse {

    private final Node node;
    private double timeStamp;
    private final Vec2 prev = new Vec2(),
            current = new Vec2(),
            delta = new Vec2();

    public static Mouse reg(Node node) {
        Mouse m = new Mouse(node);
        node.setOnMouseEntered(m::prev);
        return m;
    }

    private Mouse(Node node) {
        this.node = node;
    }

    private void prev(MouseEvent e) {
        prev.x = e.getX();
        prev.y = e.getY();
    }

    private boolean hasMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        if (x != prev.x || y != prev.y) {
            current.x = x;
            current.y = y;
            delta.x = x - prev.x;
            delta.y = y - prev.y;
            return true;
        }
        return false;
    }

    public Vec2 getMovement() {
        prev.x = current.x;
        prev.y = current.y;
        return delta;
    }

    public Mouse press(int button, EventHandler<MouseEvent> handler) {
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (btn(e, button)) {
                prev(e);
                handler.handle(e);
            }
        });
        return this;
    }

    public Mouse click(int button, EventHandler<MouseEvent> handler) {
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (btn(e, button)) {
                if (System.currentTimeMillis() - timeStamp < 200) {
                    handler.handle(e);
                }
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (btn(e, button)) {
                timeStamp = System.currentTimeMillis();
            }
        });

        return this;
    }

    public Mouse move(int button, EventHandler<MouseEvent> handler) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (hasMoved(e)) {
                handler.handle(e);
            }
        });
        return this;
    }

    public Mouse drag(int button, EventHandler<MouseEvent> handler) {
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (btn(e, button) && hasMoved(e)) {
                handler.handle(e);
            }
        });
        return this;
    }

    private boolean btn(MouseEvent e, int button) {
        switch (e.getButton()) {
            case PRIMARY:
                return button == 0;
            case MIDDLE:
                return button == 1;
            case SECONDARY:
                return button == 2;
        }
        return false;
    }
}
