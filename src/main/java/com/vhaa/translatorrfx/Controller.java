package com.vhaa.translatorrfx;

import com.vhaa.translatorrfx.utils.FileData;
import com.vhaa.translatorrfx.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public static final String ORIGINAL_FILES = "./originalFiles/";
    public static final String TRANSLATION_FILES = "./translations/";

    @FXML
    private ListView<FileData> listOriginalFileData = new ListView<>();
    private List<FileData> dataList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadListFileOrigin();
        dataList.forEach( l -> crearTraduccion(l) );
    }

    private void loadListFileOrigin() {
        dataList = FileUtils.getListaFileData(Paths.get(ORIGINAL_FILES));
        listOriginalFileData.setItems(FXCollections.observableArrayList(dataList));
    }

    private void crearTraduccion(FileData l)   {

        String nameFile = l.getFileName().split("\\.")[0];
        // TODO: Busque una subcarpeta (dentro de la carpeta de imágenes) con
        //  el mismo nombre que su archivo asociado. Por ejemplo, si el nombre
        //  del archivo es exampleSP.txt, entonces debe buscar una carpeta llamada
        //  exampleSP. Si la carpeta existe, debe eliminarse (utilizando el método FileUtils.deleteDirectory)
        FileUtils.getListaFileData(Paths.get(TRANSLATION_FILES)).stream().forEach(
                d -> {
                    if(d.getFileName().equalsIgnoreCase(nameFile)){
                        try {
                            FileUtils.deleteDirectory(Paths.get(TRANSLATION_FILES.concat(nameFile)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // TODO: Cree, nuevamente, la carpeta previamente eliminada, para que esté vacía ahora
        //  (puede usar el método Files.createDirectory para esto)

        try {

            Path path = Paths.get(TRANSLATION_FILES.concat(nameFile));
            Files.createDirectory( path, new FileAttribute[] {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Detectar el idioma original del archivo actual: puedes leer la primera línea del archivo
        //  y buscarlo en los diferentes idiomas de la lista, cuando lo encontremos tendremos disponible
        //  el objeto Idioma para hacer la traducción.


        // TODO: Leer todo el archivo y traducir línea por línea, escribiendo el resultado en un nuevo
        //  archivo llamado: language.to + ”_” + nombre del archivo original.


        // TODO: Una vez finalizados todos estos procesos de traducción, el hilo debe usar el método
        //  Platform.runLater para agregar el nombre de archivo original del archivo a la vista de lista
        //  de la izquierda


    }
}