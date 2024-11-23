package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.handlers.*;
import com.github.reugn.devtools.utils.Elements;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import static com.github.reugn.devtools.utils.ControllerUtils.getFileExtension;
import static com.github.reugn.devtools.utils.ControllerUtils.range;
import static com.github.reugn.devtools.utils.ControllerUtils.resizeImage;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class ImageEditorController implements Initializable {

    private static final Logger log = LogManager.getLogger(ImageEditorController.class);
    private static final int DEFAULT_CANVAS_WIDTH = 720;
    private static final int DEFAULT_CANVAS_HEIGHT = 480;

    private final SnapshotParameters snapshotParameters = new SnapshotParameters();
    private final FileChooser fileChooser = new FileChooser();

    @FXML
    public ChoiceBox<Integer> lineWidthChoiceBox;
    @FXML
    public ColorPicker colorPicker;
    @FXML
    public ChoiceBox<Integer> zoomChoiceBox;
    @FXML
    public BorderPane canvasBorderPane;
    @FXML
    public ScrollPane canvasScrollPane;
    @FXML
    public Canvas canvas;
    @FXML
    public TextField canvasWidthField;
    @FXML
    public TextField canvasHeightField;
    @FXML
    public Button canvasDimensionsButton;
    @FXML
    public Label coordinatesLabel;
    @FXML
    public Button undoButton;
    @FXML
    public Button redoButton;
    @FXML
    public Button clearButton;
    @FXML
    public Button brushButton;
    @FXML
    public HBox toolSettingsContainer;
    @FXML
    public GridPane geometryGridPane;
    @FXML
    public GridPane densityGridPane;
    @FXML
    public TextField dashedTextField;
    @FXML
    public CheckBox fillCheckBox;
    @FXML
    public TextField densityTextField;

    private List<WritableImage> snapshots = new ArrayList<>();
    private Tool tool = Tool.BRUSH;
    private Button selectedToolButton;
    private int snapshotIndex;
    private double startX;
    private double startY;
    private GraphicsContext gc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lineWidthChoiceBox.getItems().addAll(IntStream.range(1, 11).boxed().collect(toList()));
        lineWidthChoiceBox.getItems().addAll(15, 20, 30, 50);
        lineWidthChoiceBox.getSelectionModel().select(Integer.valueOf(3));

        zoomChoiceBox.getItems().addAll(range(10, 100, 10));
        zoomChoiceBox.getItems().add(150);
        zoomChoiceBox.getItems().addAll(range(200, 500, 100));
        resetZoomChoiceBox();

        colorPicker.setValue(Color.BLACK);

        canvas.setCursor(tool.cursor);
        resetCanvasDimensions();

        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        snapshotParameters.setFill(Color.WHITE);

        snapshots.add(takeCanvasSnapshot());
        undoButton.setDisable(true);
        redoButton.setDisable(true);

        selectedToolButton = brushButton;
        toolSettingsContainer.getChildren().clear();
    }

    public WritableImage takeCanvasSnapshot() {
        return canvas.snapshot(snapshotParameters, null);
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public WritableImage getCurrentSnapshot() {
        return snapshots.get(snapshotIndex);
    }

    private void resetCanvasDimensions() {
        clearBorders();
        canvasHeightField.setText(String.valueOf(DEFAULT_CANVAS_HEIGHT));
        canvasWidthField.setText(String.valueOf(DEFAULT_CANVAS_WIDTH));
        canvasDimensionsButton.setDisable(false);
        canvas.setHeight(Double.parseDouble(canvasHeightField.getText()));
        canvas.setWidth(Double.parseDouble(canvasWidthField.getText()));
    }

    private void clearBorders() {
        canvasHeightField.setBorder(Border.EMPTY);
        canvasWidthField.setBorder(Border.EMPTY);
    }

    private void resetZoomChoiceBox() {
        zoomChoiceBox.setOnAction(null);
        zoomChoiceBox.getSelectionModel().select(Integer.valueOf(100));
        zoomChoiceBox.setOnAction(this::handleZoom);
    }

    @SuppressWarnings("unused")
    public void handleLoadImage(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(canvasBorderPane.getScene().getWindow());
        if (file == null) {
            return;
        }
        Image image = new Image(file.toURI().toString());
        if (image.isError()) {
            String message = format("Failed to load image from %s", file.getAbsolutePath());
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.showAndWait();
            return;
        }
        redoButton.setDisable(true);
        drawImage(image);
        addSnapshot();
    }

    @SuppressWarnings("unused")
    public void handleSaveImage(ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(canvasBorderPane.getScene().getWindow());
        if (file == null) {
            return;
        }
        WritableImage image = getCurrentSnapshot();
        try {
            String formatName = getFileExtension(file);
            if (!ImageIO.write(SwingFXUtils.fromFXImage(image, null), formatName, file)) {
                throw new IOException(format("No appropriate writer is found for: %s", formatName));
            }
        } catch (IOException e) {
            String message = format("Failed to store image to %s, error: %s",
                    file.getAbsolutePath(), e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.showAndWait();
        }
    }

    @SuppressWarnings("unused")
    public void handleUndoImage(ActionEvent actionEvent) {
        WritableImage image = snapshots.get(--snapshotIndex);
        log.info("handleUndoImage: {}", snapshotIndex);
        if (snapshotIndex == 0) {
            undoButton.setDisable(true);
            canvasDimensionsButton.setDisable(false);
        }
        redoButton.setDisable(false);
        drawImage(image);
    }

    @SuppressWarnings("unused")
    public void handleRedoImage(ActionEvent actionEvent) {
        WritableImage image = snapshots.get(++snapshotIndex);
        log.info("handleRedoImage: {}", snapshotIndex);
        if (snapshotIndex == snapshots.size() - 1) {
            redoButton.setDisable(true);
        }
        undoButton.setDisable(false);
        drawImage(image);
    }

    public void drawImage(Image image) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (canvas.getWidth() != image.getWidth()) {
            canvas.setWidth(image.getWidth());
        }
        if (canvas.getHeight() != image.getHeight()) {
            canvas.setHeight(image.getHeight());
        }

        updateCanvasDimensions();

        gc.drawImage(image, 0, 0);
        log.info("drawImage");
    }

    private void updateCanvasDimensions() {
        clearBorders();
        canvasWidthField.setText(String.valueOf((int) canvas.getWidth()));
        canvasHeightField.setText(String.valueOf((int) canvas.getHeight()));
    }

    @SuppressWarnings("unused")
    public void handleClearImage(ActionEvent actionEvent) {
        log.info("handleClearImage");
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        resetCanvasDimensions();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        resetZoomChoiceBox();
        undoButton.setDisable(true);
        redoButton.setDisable(true);

        snapshots = snapshots.subList(0, 1);
        snapshotIndex = 0;
    }

    public void handleMouseMoved(MouseEvent mouseEvent) {
        tool.handler.mouseMoved(this, mouseEvent);
    }

    public void handleMousePressed(MouseEvent mouseEvent) {
        tool.handler.mousePressed(this, mouseEvent);
    }

    public void handleMouseReleased(MouseEvent mouseEvent) {
        tool.handler.mouseReleased(this, mouseEvent);
    }

    public void handleMouseDragged(MouseEvent mouseEvent) {
        tool.handler.mouseDragged(this, mouseEvent);
    }

    public void handleMouseExited(MouseEvent mouseEvent) {
        tool.handler.mouseExited(this, mouseEvent);
    }

    public void setStartCoordinates(MouseEvent mouseEvent) {
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();
    }

    @SuppressWarnings({"unused", "java:S1659"})
    public void handleCanvasDimensions(ActionEvent actionEvent) {
        clearBorders();
        double width, height;
        try {
            width = Double.parseDouble(canvasWidthField.getText());
        } catch (NumberFormatException e) {
            canvasWidthField.setBorder(Elements.alertBorder);
            return;
        }
        try {
            height = Double.parseDouble(canvasHeightField.getText());
        } catch (NumberFormatException e) {
            canvasHeightField.setBorder(Elements.alertBorder);
            return;
        }
        canvas.setWidth(width);
        canvas.setHeight(height);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        canvas.requestFocus();
        addSnapshot();
    }

    public void handleZoom(ActionEvent actionEvent) {
        log.info("handleZoom {}", zoomChoiceBox.getValue());
        double scaleValue = (double) zoomChoiceBox.getValue() / 100;
        WritableImage originalImage = getCurrentSnapshot();
        double newWidth = (int) (originalImage.getWidth() * scaleValue);
        double newHeight = (int) (originalImage.getHeight() * scaleValue);

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setHeight(newHeight);
        canvas.setWidth(newWidth);

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);
        BufferedImage resizedImage = resizeImage(bufferedImage, (int) newWidth, (int) newHeight);
        gc.drawImage(SwingFXUtils.toFXImage(resizedImage, null), 0, 0);

        updateCanvasDimensions();
        addSnapshot();
    }

    @SuppressWarnings("unused")
    public void handleRotateLeft(ActionEvent actionEvent) {
        rotateImage(-90);
    }

    @SuppressWarnings("unused")
    public void handleRotateRight(ActionEvent actionEvent) {
        rotateImage(90);
    }

    private void rotateImage(int degrees) {
        WritableImage originalImage = getCurrentSnapshot();
        ImageView imageView = new ImageView(originalImage);
        imageView.setRotate(degrees);
        Image rotatedImage = imageView.snapshot(snapshotParameters, null);
        drawImage(rotatedImage);
        canvas.requestFocus();
        addSnapshot();
    }

    @SuppressWarnings("unused")
    public void handleInvertColors(ActionEvent actionEvent) {
        WritableImage originalImage = getCurrentSnapshot();
        PixelReader reader = originalImage.getPixelReader();
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage invertedImage = new WritableImage(width, height);
        PixelWriter writer = invertedImage.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, y, color.invert());
            }
        }

        drawImage(invertedImage);
        canvas.requestFocus();
        addSnapshot();
    }

    public void handleToolSelection(ActionEvent actionEvent) {
        Button toolButton = (Button) actionEvent.getSource();
        String toolName = toolButton.getUserData().toString();
        selectedToolButton.setDisable(false);
        selectedToolButton = toolButton;
        selectedToolButton.setDisable(true);
        tool = Tool.valueOf(toolName);
        canvas.setCursor(tool.cursor);
        canvas.requestFocus();
        log.info("Selected {}", tool);

        toolSettingsContainer.getChildren().clear();
        switch (tool) {
            case LINE:
            case RECTANGLE:
            case ELLIPSE:
                toolSettingsContainer.getChildren().add(geometryGridPane);
                break;
            case AIR_BRUSH:
                toolSettingsContainer.getChildren().add(densityGridPane);
                break;
            default:
                break;
        }
    }

    public void updateCoordinates(MouseEvent mouseEvent) {
        coordinatesLabel.setText(format("%.0f: %.0f", mouseEvent.getX(), mouseEvent.getY()));
    }

    public void addSnapshot() {
        if (snapshotIndex == 0) {
            undoButton.setDisable(false);
            redoButton.setDisable(true);
            canvasDimensionsButton.setDisable(true);
        }
        if (snapshotIndex < snapshots.size() - 1) {
            snapshots = snapshots.subList(0, snapshotIndex + 1);
            redoButton.setDisable(true);
        }
        snapshots.add(takeCanvasSnapshot());
        snapshotIndex++;
        log.info("addSnapshot {}, {}", snapshotIndex, snapshots.size());
    }

    private enum Tool {
        BRUSH(Cursor.CROSSHAIR, new ImageEditorBrushHandler()),
        COLOR_FILL(Cursor.CROSSHAIR, new ImageEditorColorFillHandler()),
        LINE(Cursor.CROSSHAIR, new ImageEditorLineHandler()),
        RECTANGLE(Cursor.CROSSHAIR, new ImageEditorRectangleHandler()),
        ELLIPSE(Cursor.CROSSHAIR, new ImageEditorEllipseHandler()),
        AIR_BRUSH(Cursor.CROSSHAIR, new ImageEditorAirBrushHandler()),
        EYEDROPPER(Cursor.CROSSHAIR, new ImageEditorEyedropperHandler()),
        CROP(Cursor.CROSSHAIR, new ImageEditorCropHandler()),
        ERASER(Cursor.CROSSHAIR, new ImageEditorEraserHandler());

        private final Cursor cursor;
        private final ImageEditorMouseEventHandler handler;

        Tool(Cursor cursor, ImageEditorMouseEventHandler handler) {
            this.cursor = cursor;
            this.handler = handler;
        }
    }
}
