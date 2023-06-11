package com.uniproject.oop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.FlowPane;

enum TileType {
    EMPTY,
    BOMB,
    COUNT;
}

final class Tile {
    Button btn;
    boolean isOpened;
    boolean isMarked;
    TileType tileType;
    int count;

    Tile(Button btn, boolean isOpened, int count, TileType tileType) {
        this.btn = btn;
        this.tileType = tileType;
        this.isOpened = isOpened;
        this.count = count;
    }

}

public class GameController {
    @FXML
    private FlowPane flowpane;

    List<Integer> bombLocations = new ArrayList<>(App.totalBombs);
    List<Tile> tiles = new ArrayList<>(App.tilesPerCol * App.tilesPerRow);
    int openedTiles = 0;
    boolean firstMove;

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("start_screen");
    }

    public void initialize() throws IOException {

        try {
            generateTiles();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private int generateBombLocation(Random rand) {
        int r = rand.nextInt((App.tilesPerCol * App.tilesPerRow));
        int bombX = r % App.tilesPerRow;
        int bombY = r / App.tilesPerCol;
        return (bombX * App.tilesPerRow) + bombY;
    }

    @FXML
    private void generateTiles() throws IOException {
        var rand = new Random();
        // Generate Mines
        for (int x = 0; x < App.totalBombs; x++) {
            bombLocations.add(generateBombLocation(rand));
        }

        for (int x = 0; x < (App.tilesPerCol * App.tilesPerRow); x++) {
            Button btn = new Button();
            btn.setPrefSize(App.tileSize, App.tileSize);

            final int index = x;
            EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() {

                public void handle(MouseEvent e) {
                    try {
                        if (e.getButton() == MouseButton.PRIMARY) {
                            if (tiles.get(index).isMarked) {
                                return;
                            }
                            openTile(index);
                        } else if (e.getButton() == MouseButton.SECONDARY && !tiles.get(index).isOpened) {
                            if (tiles.get(index).isMarked) {
                                tiles.get(index).btn.setGraphic(null);
                                tiles.get(index).isMarked = false;
                                return;
                            }
                            Image markingImage = new Image(getClass().getResourceAsStream("assets/flag.png"),
                                    App.tileSize,
                                    App.tileSize, false, true);

                            tiles.get(index).btn.setGraphic(new ImageView(markingImage));
                            tiles.get(index).btn.setPadding(Insets.EMPTY);
                            tiles.get(index).isMarked = true;
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            };
            btn.setOnMouseClicked(event);

            if (bombLocations.contains(index)) {
                tiles.add(new Tile(btn, false, 0, TileType.BOMB));
            }

            else {
                int count = countSurroundingBombs(index);
                tiles.add(new Tile(btn, false, count, count > 0 ? TileType.COUNT : TileType.EMPTY));
            }

            flowpane.getChildren().add(btn);
        }

    }

    private void openTile(int index) throws IOException {

        Tile tile = tiles.get(index);
        if (tile.isOpened)
            return;

        tile.isOpened = true;

        // Check if the current tile contains a bomb
        if (tile.tileType == TileType.BOMB) {
            App.setRoot("game_over");
        } else if (tile.tileType == TileType.EMPTY) {
            openedTiles++;
            tile.btn.setStyle(
                    "-fx-background-color: LightGray; -fx-border-color: gray;");
            openNeighboringTiles(index);
        } else {
            openedTiles++;
            String imgPath = "assets/";
            switch (tile.count) {
                case 1:
                    imgPath += "1.png";
                    break;
                case 2:
                    imgPath += "2.png";
                    break;
                case 3:
                    imgPath += "3.png";
                    break;
                case 4:
                    imgPath += "4.png";
                    break;
                case 5:
                    imgPath += "5.png";
                    break;
                case 6:
                    imgPath += "6.png";
                    break;
                case 7:
                    imgPath += "7.png";
                    break;
                case 8:
                    imgPath += "8.png";
                    break;
            }
            Image image = new Image(getClass()
                    .getResourceAsStream(imgPath), App.tileSize, App.tileSize, false, true);
            tile.btn.setPadding(Insets.EMPTY);
            tile.btn.setGraphic(new ImageView(image));
        }

        int totalNonBombTiles = (App.tilesPerCol * App.tilesPerRow) - App.totalBombs;
        if (openedTiles == totalNonBombTiles) {
            // Player has revealed all non-bomb tiles and won the game
            App.setRoot("game_won");
        }

    }

    private int countSurroundingBombs(int tileIndex) {
        int count = 0;
        int[] neighbors = { -1, 0, 1 };

        int tileX = tileIndex % App.tilesPerRow;
        int tileY = tileIndex / App.tilesPerRow;

        for (int xOffset : neighbors) {
            for (int yOffset : neighbors) {
                int neighborX = tileX + xOffset;
                int neighborY = tileY + yOffset;

                // Skip the current tile
                if (xOffset == 0 && yOffset == 0) {
                    continue;
                }

                if (neighborX >= 0 && neighborX < App.tilesPerRow && neighborY >= 0 && neighborY < App.tilesPerCol) {
                    int neighborIndex = (neighborY * App.tilesPerRow) + neighborX;
                    if (bombLocations.contains(neighborIndex)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private void openNeighboringTiles(int index) throws IOException {
        int[] neighbors = { -1, 0, 1 };

        int tileX = index % App.tilesPerRow;
        int tileY = index / App.tilesPerRow;

        for (int xOffset : neighbors) {
            for (int yOffset : neighbors) {
                int neighborX = tileX + xOffset;
                int neighborY = tileY + yOffset;

                // Skip the current tile
                if (xOffset == 0 && yOffset == 0) {
                    tiles.get(index).btn.setText("");
                    continue;
                }

                if (neighborX >= 0 && neighborX < App.tilesPerRow && neighborY >= 0 && neighborY < App.tilesPerCol) {
                    int neighborIndex = (neighborY * App.tilesPerRow) + neighborX;

                    Tile neighborTile = tiles.get(neighborIndex);

                    if (neighborTile.isOpened || neighborTile.tileType == TileType.BOMB) {
                        continue; // Skip already opened tiles and bombs
                    }

                    openTile(neighborIndex);

                    // If the neighbor tile has a count, continue opening its neighbors
                    if (neighborTile.tileType == TileType.COUNT && neighborTile.count == 0) {
                        openNeighboringTiles(neighborIndex);
                    }
                }
            }
        }
    }

}
