/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.ColorBlindType;
import modelo.ImageFilter;
import modelo.PicsumApiClient;
import modelo.SimulacionRepository;
import vista.MainView;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


// CONTROLADOR — MainController

public class MainController {

 
    private final ImageFilter model;
    private final MainView    view;
    private final Stage                stage;
    private final SimulacionRepository repository = new SimulacionRepository();
    private String currentImageName = "desconocido";


    // Construye el controlador y enlaza los eventos de la vista.
    public MainController(ImageFilter model, MainView view, Stage stage) {
        this.model = model;
        this.view  = view;
        this.stage = stage;

        bindEvents();
    }


    private void bindEvents() {
        view.setOnLoadImage(e      -> handleLoadImage());
        view.setOnApplyFilter(type -> handleApplyFilter(type));
        view.setOnSaveImage(e      -> handleSaveImage());
        view.setOnLoadFromApi(e    -> handleLoadFromApi());
        view.setOnReset(e          -> handleReset());
        view.setOnShowHistorial(e  -> handleShowHistorial());
    }


    private void handleLoadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter(
                "Imágenes", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif", "*.tiff"
            ),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());

                if (image.isError()) {
                    view.showError("No se pudo cargar la imagen.\nVerifica que el archivo sea válido.");
                    return;
                }

                model.setOriginalImage(image);
            currentImageName = "Picsum_API_" + (int)image.getWidth() + "x" + (int)image.getHeight();
                currentImageName = selectedFile.getName();
                view.displayOriginalImage(image);
                view.setFilterButtonsEnabled(true);
                view.setResetEnabled(false);
                view.setSaveEnabled(false);
                view.clearProcessedImage();
                view.showStatus("Imagen cargada: " + selectedFile.getName()
                    + "  (" + (int) image.getWidth() + " × " + (int) image.getHeight() + " px)");

            } catch (Exception ex) {
                view.showError("Error al leer el archivo:\n" + ex.getMessage());
            }
        }
    }

    private void handleApplyFilter(ColorBlindType type) {
        if (!model.hasImage()) {
            view.showError("Primero debes cargar una imagen.");
            return;
        }

        view.setProcessing(true);
        view.showStatus("Procesando simulación de " + type.getDisplayName() + "...");

        Task<Image> task = new Task<>() {
            @Override
            protected Image call() {
                return model.applyColorBlindFilter(type);
            }
        };

        task.setOnSucceeded(e -> {
            Image result = task.getValue();
            view.displayProcessedImage(result);
            view.setProcessing(false);
            view.setResetEnabled(true);
            view.setSaveEnabled(true);
            String nombreFuente = currentImageName;
            repository.guardarSimulacion(
                type.getDisplayName(),
                nombreFuente,
                (int) task.getValue().getWidth(),
                (int) task.getValue().getHeight()
            );
            view.showStatus("Simulación aplicada: " + type.getDisplayName()
                + " — Así ve esta imagen una persona con este tipo de daltonismo.");
        });

        task.setOnFailed(e -> {
            view.setProcessing(false);
            view.showError("Error durante el procesamiento:\n"
                + task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    private void handleSaveImage() {
        Image processedImage = model.getProcessedImage();

        if (processedImage == null) {
            view.showError("No hay imagen procesada para guardar.\nAplica un filtro primero.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Imagen Procesada");
        fileChooser.setInitialFileName("simulacion_" + model.getCurrentType().name().toLowerCase() + ".png");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG (sin pérdida)", "*.png"),
            new FileChooser.ExtensionFilter("JPEG",              "*.jpg"),
            new FileChooser.ExtensionFilter("BMP",               "*.bmp")
        );

        File destFile = fileChooser.showSaveDialog(stage);

        if (destFile != null) {
            try {
                String extension = getExtension(destFile.getName());
                String format    = extension.equalsIgnoreCase("jpg") ? "jpeg" : extension;

                BufferedImage buffered = SwingFXUtils.fromFXImage(processedImage, null);

                if (format.equalsIgnoreCase("jpeg")) {
                    BufferedImage rgbImage = new BufferedImage(
                        buffered.getWidth(), buffered.getHeight(), BufferedImage.TYPE_INT_RGB
                    );
                    rgbImage.createGraphics().drawImage(buffered, 0, 0, java.awt.Color.WHITE, null);
                    buffered = rgbImage;
                }

                ImageIO.write(buffered, format, destFile);

                view.showStatus("Imagen guardada: " + destFile.getName());
                view.showSuccess("La imagen se guardó correctamente en:\n" + destFile.getAbsolutePath());

            } catch (IOException ex) {
                view.showError("No se pudo guardar la imagen:\n" + ex.getMessage());
            }
        }
    }


    //Limpia la imagen procesada de la vista y restablece el estado de los botones.
    private void handleReset() {
        view.clearProcessedImage();
        view.setResetEnabled(false);
        view.setSaveEnabled(false);
        view.showStatus("Vista restablecida. Selecciona un tipo de daltonismo para simular.");
    }

    /**
     * Descarga una imagen aleatoria desde la API de Picsum Photos en un
     * hilo secundario para no bloquear la interfaz gráfica.
     * Al completarse, la imagen se carga en el modelo y se muestra en la vista.
     */
    private void handleLoadFromApi() {
        view.setApiLoading(true);
        view.showStatus("Descargando imagen desde la API de Picsum Photos...");

        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                return new PicsumApiClient().fetchRandomImage();
            }
        };

        task.setOnSucceeded(e -> {
            Image image = task.getValue();
            model.setOriginalImage(image);
            currentImageName = "Picsum_API_" + (int)image.getWidth() + "x" + (int)image.getHeight();
            view.displayOriginalImage(image);
            view.setFilterButtonsEnabled(true);
            view.setResetEnabled(false);
            view.setSaveEnabled(false);
            view.clearProcessedImage();
            view.setApiLoading(false);
            view.showStatus("Imagen aleatoria cargada desde Picsum Photos API"
                + "  (" + (int) image.getWidth() + " × " + (int) image.getHeight() + " px)");
        });

        task.setOnFailed(e -> {
            view.setApiLoading(false);
            view.showError("No se pudo obtener la imagen desde la API.\n"
                + task.getException().getMessage());
            view.showStatus("Error al conectar con la API. Verifica tu conexión a internet.");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    /**
     * Abre una ventana modal con el historial de simulaciones almacenado en MySQL.
     * Muestra fecha, tipo de daltonismo, nombre de imagen y dimensiones.
     */
    private void handleShowHistorial() {
        java.util.List<String[]> historial = repository.obtenerHistorial();

        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Historial de Simulaciones — MySQL");
        dialog.setMinWidth(680);
        dialog.setMinHeight(420);

        javafx.scene.control.TableView<String[]> table = new javafx.scene.control.TableView<>();
        table.setStyle("-fx-background-color: #1A1D27; -fx-text-fill: #E8EAF0;");

        String[] columns = { "ID", "Fecha", "Tipo Daltonismo", "Imagen", "Ancho", "Alto" };
        int[] widths     = { 45,   145,    160,               160,      65,     65  };

        for (int i = 0; i < columns.length; i++) {
            final int idx = i;
            javafx.scene.control.TableColumn<String[], String> col =
                new javafx.scene.control.TableColumn<>(columns[i]);
            col.setPrefWidth(widths[i]);
            col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue()[idx]));
            table.getColumns().add(col);
        }

        table.getItems().addAll(historial);

        javafx.scene.control.Label totalLabel = new javafx.scene.control.Label(
            "Total de simulaciones registradas: " + repository.contarSimulaciones());
        totalLabel.setStyle("-fx-text-fill: #8892A4; -fx-font-size: 11px; -fx-font-family: 'Segoe UI';");

        javafx.scene.control.Button btnLimpiar = new javafx.scene.control.Button("🗑  Limpiar historial");
        btnLimpiar.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; "
            + "-fx-font-size: 11px; -fx-font-family: 'Segoe UI'; -fx-background-radius: 5; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> {
            if (repository.limpiarHistorial()) {
                table.getItems().clear();
                totalLabel.setText("Total de simulaciones registradas: 0");
            }
        });

        javafx.scene.control.Button btnCerrar = new javafx.scene.control.Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #2A2D3E; -fx-text-fill: #E8EAF0; "
            + "-fx-font-size: 11px; -fx-font-family: 'Segoe UI'; -fx-background-radius: 5; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> dialog.close());

        javafx.scene.layout.HBox buttonRow = new javafx.scene.layout.HBox(10, btnLimpiar, btnCerrar);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        buttonRow.setPadding(new javafx.geometry.Insets(10, 16, 10, 16));

        javafx.scene.layout.HBox statusRow = new javafx.scene.layout.HBox(totalLabel);
        statusRow.setPadding(new javafx.geometry.Insets(8, 16, 0, 16));

        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(0, table, statusRow, buttonRow);
        javafx.scene.layout.VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        root.setStyle("-fx-background-color: #0F1117;");

        dialog.setScene(new javafx.scene.Scene(root, 680, 420));
        dialog.show();
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot + 1).toLowerCase() : "png";
    }
}