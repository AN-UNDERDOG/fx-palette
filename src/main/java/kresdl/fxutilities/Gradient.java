package kresdl.fxutilities;

import javafx.scene.paint.Color;
import java.util.Map;
import java.util.TreeMap;
import kresdl.utilities.Misc;

/**
 * Color gradient
 */
public class Gradient extends TreeMap<Double, Color> {

    public static final long serialVersionUID = 1L;

    /**
     * Constructs a black to white gradient.
     */
    public Gradient() {
        put(-0.00001d, Color.BLACK);
        put(1.0d, Color.WHITE);
    }

    /**
     * Constructs a custom gradient.
     *
     * @param a color at first position
     * @param e	array of entries
     * @param b color at last position
     */
    public Gradient(Color a, Entry[] e, Color b) {
        put(-0.00001d, a);
        if (e != null) {
            for (Entry t : e) {
                put(Misc.sat(t.key), t.color);
            }
        }
        put(1.0d, b);
    }

    /**
     * Constructs a gradient from another gradient.
     *
     * @param g gradient to copy
     */
    public Gradient(Gradient g) {
        for (Map.Entry<Double, Color> t : g.entrySet()) {
            put(t.getKey(), t.getValue());
        }
    }

    /**
     * Sets start color.
     *
     * @param color start color
     */
    public void setStart(Color color) {
        put(-0.00001d, color);
    }

    /**
     * Sets end color.
     *
     * @param color end color
     */
    public void setEnd(Color color) {
        put(1.0d, color);
    }

    /**
     * Sets color.
     *
     * @param key position
     * @param color color
     */
    public void set(double key, Color color) {
        put(Misc.sat(key), color);
    }

    /**
     * Returns color components as an array of doubles. Performs no clamping on
     * parameters or result.
     *
     * @param g position
     * @param components components r, g, and b in the range 0-255
     * @return components
     */
    public double[] get(double g, double[] components) {
        Map.Entry<Double, Color> a = lowerEntry(g);
        Map.Entry<Double, Color> b = ceilingEntry(g);
        double p = (g - a.getKey()) / (b.getKey() - a.getKey());
        p = 3 * p * p - 2 * p * p * p;
        Color c1 = a.getValue();
        Color c2 = b.getValue();
        components[0] = c1.getRed() + p * (c2.getRed() - c1.getRed());
        components[1] = c1.getGreen() + p * (c2.getGreen() - c1.getGreen());
        components[2] = c1.getBlue() + p * (c2.getBlue() - c1.getBlue());
        return components;
    }

    /**
     * Returns color.
     *
     * @param g position
     * @return color
     */
    public Color get(double g) {
        g = Misc.sat(g);
        Map.Entry<Double, Color> a = lowerEntry(g);
        Map.Entry<Double, Color> b = ceilingEntry(g);
        double p = (g - a.getKey()) / (b.getKey() - a.getKey());
        p = 3 * p * p - 2 * p * p * p;
        return a.getValue().interpolate(b.getValue(), p);
    }

    /**
     * Returns closest entry.
     *
     * @param g position
     * @return entry
     */
    public Entry getClosest(double g) {
        g = Misc.sat(g);
        Map.Entry<Double, Color> a = lowerEntry(g);
        Map.Entry<Double, Color> b = ceilingEntry(g);
        double x = Math.abs(a.getKey() - g);
        double y = Math.abs(b.getKey() - g);
        Map.Entry<Double, Color> e = (x < y ? a : b);
        return new Entry(e.getKey(), e.getValue());
    }

    /**
     * Checks if an entry exists at a certain position.
     *
     * @param g position
     * @return boolean
     */
    public boolean contains(double g) {
        return containsKey(g);
    }

    /**
     * Removes the entry for this key if present.
     *
     * @param key key
     * @return the previous color associated with key, or null if there was no
     * entry
     */
    public Color delete(double key) {
        return remove(key);
    }

    /**
     * Entry to populate gradient object with.
     */
    public static class Entry {

        private double key;
        private Color color;

        /**
         * Construct entry
         *
         * @param key key
         * @param color color
         */
        public Entry(double key, Color color) {
            this.key = key;
            this.color = color;
        }

        /**
         * Get key
         *
         * @return key
         */
        public double getKey() {
            return key;
        }

        /**
         * Get color
         *
         * @return color
         */
        public Color getColor() {
            return color;
        }
    }
}
