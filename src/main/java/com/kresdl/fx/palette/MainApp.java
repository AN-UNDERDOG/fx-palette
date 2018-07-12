package com.kresdl.fx.palette;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Rectangle r = new Rectangle(200, 100);
        stage.setScene(new Scene(new Group(r)));
        stage.setResizable(false);
        
        Palette palette = Palette.create(stage);
        palette.setColor(Color.RED);
        r.fillProperty().bind(palette.colorProperty());        
        stage.show();
        stage.sizeToScene();
        if (palette.select()) {
            Color c = palette.getColor();
            System.out.println(c.toString());
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
