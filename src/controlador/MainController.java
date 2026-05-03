/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;


import modelo.ColorBlindType;
import modelo.ImageModel;
import vista.MainView;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * CONTROLADOR — MainController
 * 
 * Actúa como intermediario entre el Modelo (ImageModel) y la Vista (MainView).
 * Maneja todos los eventos de usuario y coordina el flujo de datos.
 * 
 * Responsabilidades:
 * - Gestionar la carga de imágenes mediante JFileChooser (FileChooser en JavaFX)
 * - Delegar el procesamiento al modelo
 * - Actualizar la vista con los resultados
 * - Ejecutar el procesamiento en un hilo separado (Task) para no bloquear la UI
 */
public class MainController {

    private final ImageModel model;
    private final MainView view;
    private final Stage stage;

    public MainController(ImageModel model, MainView view, Stage stage) {
        this.model = model;
        this.view  = view;
        this.stage = stage;

        bindEvents();
    }

    // ─────────────────────────────────────────────
    // Vinculación de eventos de la Vista
    // ─────────────────────────────────────────────

    /**
     * Conecta los botones de la vista con los métodos del controlador.
     */
    private void bindEvents() {
        view.setOnLoadImage(e -> handleLoadImage());
        view.setOnApplyFilter(type -> handleApplyFilter(type));
        view.setOnReset(e -> handleReset());
    }

    // ─────────────────────────────────────────────
    // Manejadores de eventos
    // ─────────────────────────────────────────────

    /**
     * Abre el diálogo de selección de archivo y carga la imagen elegida.
     * Equivalente al JFileChooser de Swing.
     */
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
                view.clearProcessedImage();
                view.showStatus("Imagen cargada: " + selectedFile.getName()
                    + "  (" + (int)image.getWidth() + " × " + (int)image.getHeight() + " px)");

            } catch (Exception ex) {
                view.showError("Error al leer el archivo:\n" + ex.getMessage());
            }
        }
    }

    /**
     * Aplica el filtro de daltonismo seleccionado.
     * El procesamiento se ejecuta en un hilo separado para mantener
     * la interfaz responsiva durante imágenes grandes.
     * 
     * @param type Tipo de daltonismo seleccionado por el usuario
     */
    private void handleApplyFilter(ColorBlindType type) {
        if (!model.hasImage()) {
            view.showError("Primero debes cargar una imagen.");
            return;
        }

        // Mostrar indicador de carga
        view.setProcessing(true);
        view.showStatus("Procesando simulación de " + type.getDisplayName() + "...");

        // Ejecutar en hilo de fondo para no congelar la UI
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

    /**
     * Restaura la vista al estado original (sin filtro aplicado).
     */
    private void handleReset() {
        view.clearProcessedImage();
        view.setResetEnabled(false);
        view.showStatus("Vista restablecida. Selecciona un tipo de daltonismo para simular.");
    }
}

