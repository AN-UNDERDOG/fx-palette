package kresdl.palette;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import kresdl.utilities.Misc;
import kresdl.fxutilities.Colors;

public class Palette extends Stage implements Initializable {

    @FXML
    StackPane sp1;

    @FXML
    Canvas canvas;

    @FXML
    Slider multiplier;

    @FXML
    Circle paint;

    @FXML
    void mouseHandler(MouseEvent e) {
        int h = SIZE / 2;
        double x = e.getX() - h;
        double y = e.getY() - h;
        double r = Math.sqrt(x * x + y * y);
        if (r > h) {
            double s = h / r;
            x *= s;
            y *= s;
        }

        moveCursor(x, y);
        abs = pick(x, y);
        double b = multiplier.getValue();
        updateColor(Color.BLACK.interpolate(abs, b));
    }

    @FXML
    void onCancel(ActionEvent e) {
        hide();
    }

    @FXML
    void onAffirm(ActionEvent e) {
        ok = true;
        hide();
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sp1.getChildren().add(cursor);
        StackPane.setAlignment(cursor, Pos.TOP_LEFT);
        cursor.setFill(Color.WHITE);
        cursor.setStroke(Color.BLACK);
        cursor.setMouseTransparent(true);
        ObservableList<PathElement> e = cursor.getElements();
        e.add(new MoveTo(0, 0));
        e.add(new LineTo(12, 5));
        e.add(new LineTo(6, 6));
        e.add(new LineTo(5, 12));
        e.add(new ClosePath());

        ColorAdjust colorAdjust = new ColorAdjust();
        canvas.setEffect(colorAdjust);

        multiplier.valueProperty().addListener((obs, old, val) -> {
            double b = val.doubleValue();
            updateColor(Color.BLACK.interpolate(abs, b));
            colorAdjust.setBrightness(b - 1.0d);
        });

        color.addListener(colorWatcher);
        color.set(Color.WHITE);

        new Thread(() -> {
            byte[] data = new byte[3 * SIZE * SIZE];
            int k = 0;
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    double[] rgb = scan(x, y);
                    Misc.red(data, k++, rgb);
                    Misc.green(data, k++, rgb);
                    Misc.blue(data, k++, rgb);
                }
            }

            Platform.runLater(() -> {
                canvas.getGraphicsContext2D().getPixelWriter()
                        .setPixels(0, 0, SIZE, SIZE, PixelFormat.getByteRgbInstance(), data, 0, 3 * SIZE);
            });
        }).start();
    }

    public static final int SIZE = 320;
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final Path cursor = new Path();
    private boolean ok;
    private Color abs;

    private final ChangeListener<Color> colorWatcher = (obs, old, val) -> {
        Data d = Data.get(val);
        moveCursor(d.x, d.y);
        abs = pick(d.x, d.y);
        if (multiplier.getValue() != d.m) {
            multiplier.setValue(d.m);
        } else {
            updateColor(Color.BLACK.interpolate(abs, d.m));
        }
    };

    private static Palette create() {
        try {
            Palette dialog = new Palette();
            FXMLLoader fxml = new FXMLLoader(Palette.class.getResource("/fxml/palette.fxml"));
            fxml.setController(dialog);
            Parent root = fxml.load();
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            scene.getStylesheets().add("/styles/palette.css");
            return dialog;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Palette create(Node node) {
        Palette p = create(node.getScene().getWindow());
        Bounds b = node.localToScreen(node.getBoundsInLocal());
        p.setX(b.getMinX() - 20);
        p.setY(b.getMaxY() + 10);
        return p;
    }

    public static Palette create(Window window) {
        Palette palette = create();
        palette.initOwner(window);
        palette.initModality(Modality.WINDOW_MODAL);
        return palette;
    }

    public boolean select() {
        showAndWait();
        return ok;
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color c) {
        color.set(c);
    }

    public boolean isCanceled() {
        return !ok;
    }

    public Palette() {
        super();
    }

    private void updateColor(Color c) {
        color.removeListener(colorWatcher);
        color.set(c);
        color.addListener(colorWatcher);
        paint.setFill(c);
    }

    private void moveCursor(double x, double y) {
        int offset = SIZE / 2;
        cursor.setTranslateX(x + offset);
        cursor.setTranslateY(y + offset);
    }

    private Color pick(double x, double y) {
        double[] polar = toPolar(x, y);
        double rad = polar[1];
        return rad == 0
                ? Color.WHITE
                : Colors.rgb(sample(polar[0], rad));
    }

    private static double[] scan(double x, double y) {
        int h = SIZE / 2;
        double[] polar = toPolar(x - h, y - h);
        double rad = polar[1];

        if (rad == 0) {
            return new double[]{1, 1, 1};
        } else if (rad > h) {
            return new double[]{0, 0, 0};
        }

        return sample(polar[0], rad);
    }

    private static double[] sample(double angle, double rad) {
        int r1 = 0,
                g1 = 0,
                b1 = 0,
                r2 = 0,
                g2 = 0,
                b2 = 0;

        double d = 2 * Math.PI / 6;
        double q;

        if ((angle >= 0) && (angle < d)) {
            r1 = 1;
            r2 = 1;
            g2 = 1;
            q = angle;
        } else if ((angle >= d) && (angle < (2 * d))) {
            r1 = 1;
            g1 = 1;
            g2 = 1;
            q = angle - d;
        } else if ((angle >= (2 * d)) && (angle < (3 * d))) {
            g1 = 1;
            g2 = 1;
            b2 = 1;
            q = angle - 2 * d;
        } else if ((angle >= (3 * d)) && (angle < (4 * d))) {
            g1 = 1;
            b1 = 1;
            b2 = 1;
            q = angle - 3 * d;
        } else if ((angle >= (4 * d)) && (angle < (5 * d))) {
            b1 = 1;
            r2 = 1;
            b2 = 1;
            q = angle - 4 * d;
        } else {
            r1 = 1;
            b1 = 1;
            r2 = 1;
            q = angle - 5 * d;
        }

        q /= d;
        double q2 = 1.0d - rad / (SIZE / 2);

        q = 3 * q * q - 2 * q * q * q;
        q2 = 3 * q2 * q2 - 2 * q2 * q2 * q2;

        double[] rgb = new double[3];
        rgb[0] = Misc.lerp(Misc.lerp(r1, r2, q), 1, q2);
        rgb[1] = Misc.lerp(Misc.lerp(g1, g2, q), 1, q2);
        rgb[2] = Misc.lerp(Misc.lerp(b1, b2, q), 1, q2);

        return rgb;
    }

    private static double[] toPolar(double x, double y) {
        double rad = Math.sqrt(x * x + y * y);
        double angle = Math.acos(-y / rad);
        angle = x < 0
                ? 2 * Math.PI - angle
                : angle;

        return new double[]{angle, rad};

    }
}

class Data {

    double x, y, m;

    Data(double x, double y, double multiplier) {
        this.x = x;
        this.y = y;
        m = multiplier;
    }

    static Data get(Color color) {
        double[] rgb = {color.getRed(), color.getGreen(), color.getBlue()};
        double min = Math.min(Math.min(rgb[0], rgb[1]), rgb[2]);

        if (min == 1.0d) {
            return new Data(0, 0, 1);
        }

        double max = Math.max(Math.max(rgb[0], rgb[1]), rgb[2]);

        if (min == max) {
            return new Data(0, 0, max);
        }

        // Saturate
        for (int i = 0; i < 3; i++) {
            rgb[i] = (rgb[i] - min) / (max - min);
        }

        double angle;
        double rad = (1.0d - min) * Palette.SIZE / 2;
        double p = 2 * Math.PI / 6;
        int m = 0;

        for (int i = 0; i < 3; i++) {
            double c1 = rgb[i];
            double c2 = rgb[(i + 1) % 3];

            if (c1 > 0) {
                m = i;
                if (c2 > 0) {
                    if (c1 > c2) {
                        double d = c2;//straighten(c2);
                        angle = p * (2 * i + d);
                    } else {
                        double d = 1 - c1;//straighten(1 - c1);
                        angle = p * (2 * i + 1 + d);

                    }
                    double[] xy = toXY(angle, rad);
                    return new Data(xy[0], xy[1], max);
                }
            }
        }
        double[] xy = toXY(p * 2 * m, rad);
        return new Data(xy[0], xy[1], max);
    }

    private static double[] toXY(double angle, double rad) {
        double[] p = new double[2];
        p[0] = Math.sin(angle) * rad;
        p[1] = -Math.cos(angle) * rad;
        return p;
    }

    private static double straighten(double p) {
        return 0.5d * (1 + 1.0d / Math.cbrt(1 - 2 * p + 2 * Math.sqrt((-1 + p) * p)) + Math.cbrt(1 - 2 * p + 2 * Math.sqrt((-1 + p) * p)));
    }
}
