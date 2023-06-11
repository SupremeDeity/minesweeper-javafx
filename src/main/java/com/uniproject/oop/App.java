package com.uniproject.oop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

// Minesweeper logo: https://www.pngegg.com/en/png-zfnah/download?height=64
// The FontStruction “MINE-SWEEPER” (https://fontstruct.com/fontstructions/show/1501665) by Gangetsha Lyx is licensed under a Creative Commons Attribution Share Alike license (http://creativecommons.org/licenses/by-sa/3.0/).
// Minesweeper Tile icons: https://commons.wikimedia.org/wiki

public class App extends Application {
    public static final int tileSize = 36;
    public static final int tilesPerRow = 10;
    public static final int tilesPerCol = 10;
    public static final int totalBombs = (tilesPerCol * tilesPerRow) / 8; // Can be made dependent on tiles per Row &
                                                                          // Col
    public static final int width = tileSize * tilesPerRow;
    public static final int height = tileSize * tilesPerCol;

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("start_screen"), width, height);
        stage.setScene(scene);
        stage.setTitle("Minesweeper");
        stage.getIcons().add(new Image(App.class.getResourceAsStream("assets/icon.png")));
        stage.setResizable(false);
        stage.show();

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}