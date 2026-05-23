/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.ColorBlindType;
import modelo.ImageFilter;
import modelo.PicsumApiClient;
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
    private final Stage       stage;


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


    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot + 1).toLowerCase() : "png";
    }
}