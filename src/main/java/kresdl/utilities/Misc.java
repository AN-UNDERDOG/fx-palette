package kresdl.utilities;

import java.awt.Color;
import java.awt.Point;

import kresdl.geometry.Vec2;

/**
 * Utilities
 */
public abstract class Misc {

    /**
     * Clamps x to range 0-1.
     *
     * @param x value
     * @return clamped value
     */
    public static double sat(double x) {
        return Math.min(Math.max(x, 0.0d), 1.0d);
    }

    /**
     * Clamps x between min and max.
     *
     * @param x value
     * @param min min value
     * @param max max value
     * @return clamped value
     */
    public static double clamp(double x, double min, double max) {
        return Math.min(Math.max(x, min), max);
    }

    /**
     * Interpolates between two values
     *
     * @param x value 1
     * @param y value 2
     * @param p gradient
     * @return interpolated value
     */
    public static double lerp(double x, double y, double p) {
        return x + p * (y - x);
    }

    /**
     * Adds two points.
     *
     * @param a point a
     * @param b point b
     * @return a + b
     */
    public static Point pointAdd(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    /**
     * Subtracts one points from another.
     *
     * @param a point a
     * @param b point b
     * @return a - b
     */
    public static Point pointSub(Point a, Point b) {
        return new Point(a.x - b.x, a.y - b.y);
    }

    /**
     * Interpolates between two colors.
     *
     * @param a color a
     * @param b color b
     * @param w weight, 0 - 1
     * @return interpolated color
     */
    public static java.awt.Color colorLerp(java.awt.Color a, java.awt.Color b, double w) {
        int red = Math.max(Math.min((int) (w * (b.getRed() - a.getRed()) + a.getRed()), 255), 0);
        int green = Math.max(Math.min((int) (w * (b.getGreen() - a.getGreen()) + a.getGreen()), 255), 0);
        int blue = Math.max(Math.min((int) (w * (b.getBlue() - a.getBlue()) + a.getBlue()), 255), 0);
        return new java.awt.Color(red, green, blue);
    }

    /**
     * Clamps color components red, green and blue to range 0-255.
     *
     * @param components the color components to be clamped represented as an
     * array of doubles.
     * @return components
     */
    public static double[] satRGB(double[] components) {
        components[0] = Math.max(Math.min(components[0], 255), 0);
        components[1] = Math.max(Math.min(components[1], 255), 0);
        components[2] = Math.max(Math.min(components[2], 255), 0);
        return components;
    }

    /**
     * Clamps components red, green, blue and alpha to range 0-255.
     *
     * @param components the color components to be clamped represented as an
     * array of doubles.
     * @return components
     */
    public static double[] satRGBA(double[] components) {
        components[0] = Math.max(Math.min(components[0], 255), 0);
        components[1] = Math.max(Math.min(components[1], 255), 0);
        components[2] = Math.max(Math.min(components[2], 255), 0);
        components[3] = Math.max(Math.min(components[3], 255), 0);
        return components;
    }

    /**
     * Packs color components to integer format 0xAARRGGBB with an alpha of
     * 0xff.
     *
     * @param r red
     * @param g green
     * @param b blue
     * @return 0xAARRGGBB
     */
    public static int toRGB(int r, int g, int b) {
        return (0xff & b) + (0xff00 & (g << 8)) + (0xff0000 & (r << 16)) + 0xff000000;
    }

    /**
     * Packs components to integer format 0xAARRGGBB.
     *
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     * @return 0xAARRGGBB
     */
    public static int toARGB(int r, int g, int b, int a) {
        return (0xff & b) + (0xff00 & (g << 8)) + (0xff0000 & (r << 16)) + (0xff000000 & (a << 24));
    }

    /**
     * Packs color components to integer format 0xAARRGGBB with an alpha of
     * 0xff.
     *
     * @param c components r, g and b in the range 0-255 as an array of doubles
     * @return 0xAARRGGBB
     */
    public static int toRGB(double[] c) {
        return toRGB((int) c[0], (int) c[1], (int) c[2]);
    }

    /**
     * Packs components to integer format 0xAARRGGBB.
     *
     * @param c components r, g, b and a in the range 0-255 as an array of
     * doubles
     * @return 0xAARRGGBB
     */
    public static int toARGB(double[] c) {
        return toARGB((int) c[0], (int) c[1], (int) c[2], (int) c[3]);
    }

    /**
     * Constructs a qubic bezier curve.
     *
     * @param c array consisting of 4 control points.
     * @param n number of curve points to be generated.
     * @param p array of size n + 2 to be used for generating curve points, end
     * points included. If null, an array will be allocated.
     * @return array consisting of n + 2 curve points, end points included.
     */
    public static Point[] bezier(Point[] c, int n, Point[] p) {
        Vec2 u = new Vec2(c[0]);
        Vec2 v = new Vec2(c[1]);
        Vec2 w = new Vec2(c[2]);
        Vec2 z = new Vec2(c[3]);

        n++;
        Vec2 d1 = Vec2.div(Vec2.sub(v, u), n);
        Vec2 d2 = Vec2.div(Vec2.sub(w, v), n);
        Vec2 d3 = Vec2.div(Vec2.sub(z, w), n);

        if (p == null) {
            p = new Point[n + 2];
        }

        p[0] = new Point((int) u.x, (int) u.y);

        for (int i = 1; i < n; i++) {
            u = Vec2.add(u, d1);
            v = Vec2.add(v, d2);
            w = Vec2.add(w, d3);
            Vec2 r = Vec2.add(u, Vec2.mul(Vec2.div(Vec2.sub(v, u), n), i));
            Vec2 s = Vec2.add(v, Vec2.mul(Vec2.div(Vec2.sub(w, v), n), i));
            Vec2 t = Vec2.add(r, Vec2.mul(Vec2.div(Vec2.sub(s, r), n), i));
            p[i] = new Point((int) t.x, (int) t.y);
        }

        p[n] = new Point((int) z.x, (int) z.y);
        return p;
    }

    /**
     * Adds red component to byte array at index.
     *
     * @param array destination array
     * @param index offset into destination array
     * @param rgba source array of doubles in RGBA order and range 0.0 - 1.0
     */
    public static void red(byte[] array, int index, double[] rgba) {
        array[index] = (byte) (255 * rgba[0]);
    }

    /**
     * Adds green component to byte array at index.
     *
     * @param array destination array
     * @param index offset into destination array
     * @param rgba source array of doubles in RGBA order and range 0.0 - 1.0
     */
    public static void green(byte[] array, int index, double[] rgba) {
        array[index] = (byte) (255 * rgba[1]);
    }

    /**
     * Adds blue component to byte array at index.
     *
     * @param array destination array
     * @param index offset into destination array
     * @param rgba source array of doubles in RGBA order and range 0.0 - 1.0
     */
    public static void blue(byte[] array, int index, double[] rgba) {
        array[index] = (byte) (255 * rgba[2]);
    }

    /**
     * Adds alpha component to byte array at index.
     *
     * @param array destination array
     * @param index offset into destination array
     * @param rgba source array of doubles in RGBA order and range 0.0 - 1.0
     */
    public static void alpha(byte[] array, int index, double[] rgba) {
        array[index] = (byte) (255 * rgba[3]);
    }

    /**
     * Gets red component as double from Color object
     *
     * @param c Color
     * @return red component in range 0.0 - 1.0
     */
    public static double red(Color c) {
        return (double) c.getRed() / 255;
    }

    /**
     * Gets green component as double from Color object.
     *
     * @param c Color
     * @return green component in range 0.0 - 1.0
     */
    public static double green(Color c) {
        return (double) c.getGreen() / 255;
    }

    /**
     * Gets blue component as double from Color object.
     *
     * @param c Color
     * @return blue component in range 0.0 - 1.0
     */
    public static double blue(Color c) {
        return (double) c.getBlue() / 255;
    }

    /**
     * Gets alpha component as double from Color object.
     *
     * @param c Color
     * @return alpha component in range 0.0 - 1.0
     */
    public static double alpha(Color c) {
        return (double) c.getAlpha() / 255;
    }

    /**
     * Gets Color object from array of doubles.
     * @param rgb RGB component array in range 0.0 - 1.0
     * @return Color
     */
    public static Color rgb(double[] rgb) {
        return new Color((float) rgb[0], (float) rgb[1], (float) rgb[2]);
    }

    /**
     * Gets Color object from array of doubles.
     * @param rgba RGBA component array in range 0.0 - 1.0
     * @return Color
     */

    public static Color rgba(double[] rgba) {
        return new Color((float) rgba[0], (float) rgba[1], (float) rgba[2], (float) rgba[3]);
    }
}
