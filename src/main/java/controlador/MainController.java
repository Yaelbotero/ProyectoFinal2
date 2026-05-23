/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author cript
 */

import modelo.ColorBlindType;
import modelo.ImageFilter;
import modelo.ImageModel;
import vista.MainView;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


 //CONTROLADOR — MainController


public class MainController {

// Referencia al modelo a través de su interfaz
    
    private final ImageFilter model;
    private final MainView    view;
    private final Stage       stage;

  
    
    //Construye el controlador y enlaza los eventos de la vista.
    
    public MainController(ImageFilter model, MainView view, Stage stage) {
        this.model = model;
        this.view  = view;
        this.stage = stage;

        bindEvents();
    }


    //Enlaza cada acción de la vista con su manejador correspondiente.
    private void bindEvents() {
        view.setOnLoadImage(e -> handleLoadImage());
        view.setOnApplyFilter(type -> handleApplyFilter(type));
        view.setOnReset(e -> handleReset());
    }


    //Abre un diálogo para seleccionar un archivo de imagen, lo carga en el modelo y actualiza la vista.

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
                    + "  (" + (int) image.getWidth() + " × " + (int) image.getHeight() + " px)");

            } catch (Exception ex) {
                view.showError("Error al leer el archivo:\n" + ex.getMessage());
            }
        }
    }

     //Aplica el filtro de daltonismo seleccionado en un hilo secundario para no bloquear el hilo de la interfaz gráfica.

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


    //Limpia la imagen procesada de la vista y restablece el estado de los botones.
    
    private void handleReset() {
        view.clearProcessedImage();
        view.setResetEnabled(false);
        view.showStatus("Vista restablecida. Selecciona un tipo de daltonismo para simular.");
    }
}
