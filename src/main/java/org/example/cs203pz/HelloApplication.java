package org.example.cs203pz;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static final double WINDOW_WIDTH = 800;
    public static final double WINDOW_HEIGHT = 600;

    public static class RecursiveTree {
        public static final int MAX_DEPTH = 15; // Maximum recursion depth
        public static final double SUBTREE_ANGLE_OFFSET = 35.0; // Angle offset for subtrees
        public static final double SUBTREE_LENGTH_FACTOR = 0.6; // Length factor for subtrees
        public static final double TREE_ANGLE = 90.0; // Angle of the tree (Initial)
        public static final double LENGTH = 200.0; // Length of the tree (Initial)
        public static final long ANIMATION_DELAY = 100L; // Animation delay in milliseconds
        private static int recursionDepth = 0; // Recursion depth counter

        public static void Draw(final Pane pane, double x, double y, double angle, double length, int depth) {
            recursionDepth = 0;
            long time = System.currentTimeMillis();
            drawRecursive(pane, x, y, angle, length, depth);
            System.out.println("Recursion depth: " + recursionDepth + " Time taken: " + (System.currentTimeMillis() - time) + "ms");
        }

        private static void drawRecursive(final Pane pane, double x, double y, double angle, double length, int depth) {
            if (depth <= 0) return; // Base case
            recursionDepth++;

            double angleRadians = Math.toRadians(angle); // Convert angle from degrees to radians

            double xEnd = x + length * Math.cos(angleRadians); // Calculate end point x coordinate
            double yEnd = y - length * Math.sin(angleRadians); // Calculate end point y coordinate

            Line line = new Line(x, y, xEnd, yEnd); // Create line
            pane.getChildren().add(line); // Draw line from current point to end point

            drawRecursive(pane, xEnd, yEnd, angle - SUBTREE_ANGLE_OFFSET, length * SUBTREE_LENGTH_FACTOR, depth - 1); // Draw left subtree

            drawRecursive(pane, xEnd, yEnd, angle + SUBTREE_ANGLE_OFFSET, length * SUBTREE_LENGTH_FACTOR, depth - 1); // Draw right subtree
        }

        public static void DrawAnimated(final Pane pane, double x, double y, double angle, double length, int depth) {
            recursionDepth = 0;
            long time = System.currentTimeMillis();
            drawAnimatedRecursive(pane, x, y, angle, length, depth);
            System.out.println("Recursion depth: " + recursionDepth + " Time taken: " + (System.currentTimeMillis() - time) + "ms");
        }

        private static void drawAnimatedRecursive(final Pane pane, double x, double y, double angle, double length, int depth) {
            if (depth <= 0) return; // Base case
            recursionDepth++;

            double angleRadians = Math.toRadians(angle); // Convert angle from degrees to radians

            double xEnd = x + length * Math.cos(angleRadians); // Calculate end point x coordinate
            double yEnd = y - length * Math.sin(angleRadians); // Calculate end point y coordinate

            Line line = new Line(x, y, xEnd, yEnd); // Create line
            Platform.runLater(() -> pane.getChildren().add(line)); // Draw line from current point to end point

            try {
                Thread.sleep(ANIMATION_DELAY); // Sleep for ANIMATION_DELAY milliseconds
            } catch (
                    InterruptedException e) {
                throw new RuntimeException(e);
            }

            drawAnimatedRecursive(pane, xEnd, yEnd, angle - SUBTREE_ANGLE_OFFSET, length * SUBTREE_LENGTH_FACTOR, depth - 1); // Draw left subtree

            drawAnimatedRecursive(pane, xEnd, yEnd, angle + SUBTREE_ANGLE_OFFSET, length * SUBTREE_LENGTH_FACTOR, depth - 1); // Draw right subtree
        }
    }

    private int depth = 5;
    private boolean animated = false;

    @Override
    public void start(Stage stage) throws IOException {
        final BorderPane root = new BorderPane();

        final Pane renderPane = new Pane();
        renderPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setCenter(renderPane);
        BorderPane.setAlignment(renderPane, Pos.CENTER);

        root.setBottom(new HBox(new Label("Enter the depth:"), new TextField() {{
            setText(Integer.toString(5));
            RecursiveTree.Draw(renderPane, WINDOW_WIDTH * 0.5, WINDOW_HEIGHT - RecursiveTree.LENGTH * 0.5, RecursiveTree.TREE_ANGLE, RecursiveTree.LENGTH, 5);
            setOnAction(e -> {
                try {
                    depth = Math.min(RecursiveTree.MAX_DEPTH, Math.max(0, Integer.parseInt(getText()) + 1));
                    renderPane.getChildren().clear();
                    if (animated) {
                        new Thread(() -> RecursiveTree.DrawAnimated(renderPane, WINDOW_WIDTH * 0.5, WINDOW_HEIGHT - RecursiveTree.LENGTH * 0.5, RecursiveTree.TREE_ANGLE, RecursiveTree.LENGTH, depth)).start();
                    } else {
                        RecursiveTree.Draw(renderPane, WINDOW_WIDTH * 0.5, WINDOW_HEIGHT - RecursiveTree.LENGTH * 0.5, RecursiveTree.TREE_ANGLE, RecursiveTree.LENGTH, depth);
                    }
                } catch (Exception exception) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid depth");
                    alert.showAndWait();
                }
            });
        }}, new CheckBox("Animated") {{
            setOnAction(e -> animated = isSelected());
        }}) {{
            setAlignment(Pos.CENTER);
            setSpacing(10);
            setPadding(new javafx.geometry.Insets(10));
        }});

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Recursive Tree");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Stop active threads
        Thread.currentThread().interrupt();

        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}