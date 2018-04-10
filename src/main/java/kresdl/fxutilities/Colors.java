package kresdl.fxutilities;

import javafx.scene.paint.Color;

public class Colors {

    public static void red(byte[] array, int index, Color c) {
        array[index] = (byte) (255 * c.getRed());
    }

    public static void green(byte[] array, int index, Color c) {
        array[index] = (byte) (255 * c.getGreen());
    }

    public static void blue(byte[] array, int index, Color c) {
        array[index] = (byte) (255 * c.getBlue());
    }

    public static void alpha(byte[] array, int index, Color c) {
        array[index] = (byte) (255 * c.getOpacity());
    }

    public static int rgb(Color c) {
        int rgb = (int) (255 * c.getRed()) << 16;
        rgb += (int) (255 * c.getGreen()) << 8;
        return rgb + (int) (255 * c.getBlue());
    }

    public static int argb(Color c) {
        int argb = (int) (255 * c.getOpacity()) << 24;
        argb += (int) (255 * c.getRed()) << 16;
        argb += (int) (255 * c.getGreen()) << 8;
        return argb + (int) (255 * c.getBlue());
    }
    
    public static Color rgb(double[] rgb) {
        return Color.color(rgb[0], rgb[1], rgb[2]);
    }

    public static Color rgba(double[] rgba) {
        return Color.color(rgba[0], rgba[1], rgba[2], rgba[3]);
    }
}
