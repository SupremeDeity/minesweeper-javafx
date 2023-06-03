package com.uniproject.oop;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
            btn.setPrefWidth((App.width / App.tilesPerRow));
            btn.setPrefHeight((App.height / App.tilesPerCol));
            btn.setStyle(
                    "-fx-background-color: gray; -fx-border-color: black;");

            final int index = x;
            EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() {

                public void handle(MouseEvent e) {
                    try {
                        if (e.getButton() == MouseButton.PRIMARY) {
                            if (tiles.get(index).isMarked) {
                                return;
                            }
                            openTile(index);
                        } else if (e.getButton() == MouseButton.SECONDARY) {
                            if (tiles.get(index).isMarked) {
                                btn.setStyle(
                                        "-fx-background-color: gray; -fx-border-color: black;");
                                tiles.get(index).btn.setText("");
                                tiles.get(index).isMarked = false;
                                return;
                            }
                            tiles.get(index).btn.setStyle(
                                    "-fx-background-color: grey; -fx-border-color: red; -fx-text-fill: red; -fx-font-size: 1.0em;");
                            tiles.get(index).btn.setText("M");
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
        tile.btn.setStyle(
                "-fx-background-color: white; -fx-border-color: grey; -fx-text-fill: red; -fx-font-size: 1.0em;");
        tile.isOpened = true;

        // Check if the current tile contains a bomb
        if (tile.tileType == TileType.BOMB) {
            App.setRoot("game_over");
        } else if (tile.tileType == TileType.EMPTY) {
            tile.btn.setText("");
            openNeighboringTiles(index);
        } else {
            tile.btn.setText("" + tile.count);
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
