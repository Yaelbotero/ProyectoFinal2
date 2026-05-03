/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import controlador.MainController;
import modelo.ColorBlindType;
import modelo.ImageModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * VISTA — MainView
 *
 * Construye y gestiona toda la interfaz gráfica de la aplicación.
 * No contiene lógica de negocio; solo presenta datos y delega
 * los eventos al Controlador mediante callbacks funcionales.
 */
public class MainView {

    // ── Componentes principales ──────────────────────
    private final Scene scene;
    private final ImageView originalImageView;
    private final ImageView processedImageView;
    private final Label statusLabel;
    private final ProgressIndicator progressIndicator;
    private final Button loadButton;
    private final Button resetButton;
    private final Button[] filterButtons;

    // ── Callbacks hacia el Controlador ───────────────
    private javafx.event.EventHandler<javafx.event.ActionEvent> onLoadImage;
    private Consumer<ColorBlindType> onApplyFilter;
    private javafx.event.EventHandler<javafx.event.ActionEvent> onReset;

    // ── Paleta de colores ─────────────────────────────
    private static final String BG_DARK        = "#0F1117";
    private static final String BG_CARD        = "#1A1D27";
    private static final String BG_CARD2       = "#1E2130";
    private static final String ACCENT_BLUE    = "#4F8EF7";
    private static final String TEXT_PRIMARY   = "#E8EAF0";
    private static final String TEXT_SECONDARY = "#8892A4";
    private static final String BORDER_COLOR   = "#2A2D3E";

    public MainView(Stage stage) {
        originalImageView  = createImageView();
        processedImageView = createImageView();

        statusLabel = new Label("Carga una imagen para comenzar la simulación.");
        statusLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        statusLabel.setWrapText(true);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(28, 28);
        progressIndicator.setVisible(false);
        progressIndicator.setStyle("-fx-progress-color: " + ACCENT_BLUE + ";");

        filterButtons = new Button[ColorBlindType.values().length];

        loadButton  = buildLoadButton();
        resetButton = buildResetButton();

        BorderPane root = buildRoot();
        scene = new Scene(root, 1000, 700);
        scene.setFill(Color.web(BG_DARK));

        // Conectar controlador
        new MainController(new ImageModel(), this, stage);
    }

    // ─────────────────────────────────────────────────
    // Construcción del layout
    // ─────────────────────────────────────────────────

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_DARK + ";");
        root.setTop(buildHeader());
        root.setCenter(buildImageArea());
        root.setBottom(buildBottomPanel());
        return root;
    }

    private HBox buildHeader() {
        HBox icon = buildColorIcon();

        VBox titleBox = new VBox(2);
        Label title = new Label("ColorBlind Helper");
        title.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 22px; "
                     + "-fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        Label subtitle = new Label("Simulador de Daltonismo — Protanopía · Deuteranopía · Tritanopía");
        subtitle.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 11px; -fx-font-family: 'Segoe UI';");
        titleBox.getChildren().addAll(title, subtitle);

        HBox header = new HBox(14, icon, titleBox);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 28, 16, 28));
        header.setStyle("-fx-background-color: " + BG_CARD + "; "
                      + "-fx-border-color: " + BORDER_COLOR + "; "
                      + "-fx-border-width: 0 0 1 0;");
        return header;
    }

    private HBox buildColorIcon() {
        HBox box = new HBox(4);
        box.setAlignment(Pos.CENTER);
        String[] colors = { "#E74C3C", "#27AE60", "#2980B9" };
        for (String c : colors) {
            javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(8);
            circle.setFill(Color.web(c));
            circle.setEffect(new DropShadow(6, Color.web(c, 0.5)));
            box.getChildren().add(circle);
        }
        return box;
    }

    private HBox buildImageArea() {
        VBox leftPanel  = buildImagePanel(originalImageView,  "Imagen Original",   loadButton,  true);
        VBox rightPanel = buildImagePanel(processedImageView, "Imagen con Filtro", resetButton, false);

        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep.setStyle("-fx-background-color: " + BORDER_COLOR + ";");

        HBox area = new HBox(0, leftPanel, sep, rightPanel);
        HBox.setHgrow(leftPanel,  Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        area.setPadding(new Insets(20, 28, 0, 28));
        area.setStyle("-fx-background-color: " + BG_DARK + ";");
        return area;
    }

    private VBox buildImagePanel(ImageView imgView, String labelText, Button actionButton, boolean isLeft) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 11px; "
                     + "-fx-font-family: 'Segoe UI'; -fx-letter-spacing: 1px;");

        StackPane imageContainer = new StackPane(imgView);
        imageContainer.setStyle(
            "-fx-background-color: " + BG_CARD2 + "; "
          + "-fx-border-color: " + BORDER_COLOR + "; "
          + "-fx-border-width: 1; "
          + "-fx-border-radius: 8; "
          + "-fx-background-radius: 8;"
        );
        imageContainer.setMinHeight(340);
        StackPane.setMargin(imgView, new Insets(8));

        Label placeholder = new Label(isLeft ? "📁  Ninguna imagen cargada" : "👁  Aquí aparecerá la simulación");
        placeholder.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';");
        imageContainer.getChildren().add(placeholder);
        imgView.imageProperty().addListener((obs, oldImg, newImg) -> placeholder.setVisible(newImg == null));

        HBox buttonRow = new HBox(actionButton);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPadding(new Insets(10, 0, 0, 0));

        VBox panel = new VBox(8, label, imageContainer, buttonRow);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(0, isLeft ? 12 : 0, 0, isLeft ? 0 : 12));
        VBox.setVgrow(imageContainer, Priority.ALWAYS);
        return panel;
    }

    private VBox buildBottomPanel() {
        HBox filterRow = buildFilterButtons();

        HBox statusBar = new HBox(10, progressIndicator, statusLabel);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(10, 28, 12, 28));
        statusBar.setStyle(
            "-fx-background-color: " + BG_CARD + "; "
          + "-fx-border-color: " + BORDER_COLOR + "; "
          + "-fx-border-width: 1 0 0 0;"
        );

        return new VBox(0, filterRow, statusBar);
    }

    private HBox buildFilterButtons() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(16, 28, 12, 28));
        row.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label ask = new Label("Simular visión con:");
        ask.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        row.getChildren().add(ask);

        ColorBlindType[] types = ColorBlindType.values();
        for (int i = 0; i < types.length; i++) {
            ColorBlindType type = types[i];
            Button btn = buildFilterTypeButton(type);
            filterButtons[i] = btn;
            btn.setDisable(true);
            btn.setOnAction(e -> { if (onApplyFilter != null) onApplyFilter.accept(type); });

            Tooltip tooltip = new Tooltip(type.getDescription());
            tooltip.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 11px;");
            Tooltip.install(btn, tooltip);

            row.getChildren().add(btn);
        }
        return row;
    }

    private Button buildFilterTypeButton(ColorBlindType type) {
        Button btn = new Button(type.getDisplayName());
        String accent = type.getAccentColor();
        String baseStyle =
            "-fx-background-color: transparent; "
          + "-fx-border-color: " + accent + "; "
          + "-fx-border-width: 1.5; "
          + "-fx-border-radius: 6; "
          + "-fx-background-radius: 6; "
          + "-fx-text-fill: " + accent + "; "
          + "-fx-font-size: 13px; "
          + "-fx-font-weight: bold; "
          + "-fx-font-family: 'Segoe UI'; "
          + "-fx-cursor: hand;";
        String hoverStyle =
            "-fx-background-color: " + accent + "22; "
          + "-fx-border-color: " + accent + "; "
          + "-fx-border-width: 1.5; "
          + "-fx-border-radius: 6; "
          + "-fx-background-radius: 6; "
          + "-fx-text-fill: " + accent + "; "
          + "-fx-font-size: 13px; "
          + "-fx-font-weight: bold; "
          + "-fx-font-family: 'Segoe UI'; "
          + "-fx-cursor: hand;";

        btn.setPrefWidth(160);
        btn.setPrefHeight(42);
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> { if (!btn.isDisabled()) btn.setStyle(hoverStyle); });
        btn.setOnMouseExited(e  -> { if (!btn.isDisabled()) btn.setStyle(baseStyle);  });
        return btn;
    }

    private Button buildLoadButton() {
        Button btn = new Button("📁  Cargar Imagen");
        btn.setPrefWidth(160);
        btn.setPrefHeight(38);
        String base  = "-fx-background-color: " + ACCENT_BLUE + "; -fx-text-fill: white; -fx-font-size: 13px; "
                     + "-fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-background-radius: 6; -fx-cursor: hand;";
        String hover = "-fx-background-color: #3A7AE8; -fx-text-fill: white; -fx-font-size: 13px; "
                     + "-fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-background-radius: 6; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
        btn.setOnAction(e -> { if (onLoadImage != null) onLoadImage.handle(e); });
        return btn;
    }

    private Button buildResetButton() {
        Button btn = new Button("↺  Restablecer");
        btn.setPrefWidth(150);
        btn.setPrefHeight(38);
        btn.setDisable(true);
        btn.setStyle(
            "-fx-background-color: transparent; "
          + "-fx-border-color: " + TEXT_SECONDARY + "; "
          + "-fx-border-width: 1; "
          + "-fx-border-radius: 6; "
          + "-fx-background-radius: 6; "
          + "-fx-text-fill: " + TEXT_SECONDARY + "; "
          + "-fx-font-size: 12px; "
          + "-fx-font-family: 'Segoe UI'; "
          + "-fx-cursor: hand;"
        );
        btn.setOnAction(e -> { if (onReset != null) onReset.handle(e); });
        return btn;
    }

    private ImageView createImageView() {
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setFitWidth(400);
        iv.setFitHeight(320);
        return iv;
    }

    // ─────────────────────────────────────────────────
    // Métodos públicos llamados por el Controlador
    // ─────────────────────────────────────────────────

    public void displayOriginalImage(Image image)   { originalImageView.setImage(image); }
    public void displayProcessedImage(Image image)  { processedImageView.setImage(image); }
    public void clearProcessedImage()               { processedImageView.setImage(null); }
    public void setFilterButtonsEnabled(boolean en) { for (Button b : filterButtons) b.setDisable(!en); }
    public void setResetEnabled(boolean en)         { resetButton.setDisable(!en); }

    public void setProcessing(boolean processing) {
        progressIndicator.setVisible(processing);
        setFilterButtonsEnabled(!processing);
        loadButton.setDisable(processing);
    }

    public void showStatus(String message) { statusLabel.setText(message); }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ─────────────────────────────────────────────────
    // Setters de callbacks
    // ─────────────────────────────────────────────────

    public void setOnLoadImage(javafx.event.EventHandler<javafx.event.ActionEvent> h) { this.onLoadImage  = h; }
    public void setOnApplyFilter(Consumer<ColorBlindType> h)                          { this.onApplyFilter = h; }
    public void setOnReset(javafx.event.EventHandler<javafx.event.ActionEvent> h)     { this.onReset       = h; }

    public Scene getScene() { return scene; }
}
