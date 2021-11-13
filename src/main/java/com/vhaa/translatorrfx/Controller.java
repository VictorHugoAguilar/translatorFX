package com.vhaa.translatorrfx;

import com.vhaa.translatorrfx.utils.FileData;
import com.vhaa.translatorrfx.utils.FileUtils;
import com.vhaa.translatorrfx.utils.Language;
import com.vhaa.translatorrfx.utils.MessageUtils;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller implements Initializable {
    private static final String DIRECTORY_ORIGINAL = "./dictionaries/";
    private static final String FILE_LANGUAGE = "languages.txt";
    private static final String ORIGINAL_FILES = "./originalFiles/";
    private static final String DIRECTORY_TRANSLATION = "./translations/";

    private ThreadPoolExecutor executorService;
    private ScheduledService<Boolean> schedServ;

    private List<Language> dictionaries = new ArrayList<>();

    @FXML
    private ListView<FileData> listOriginalFileData = new ListView<>();
    private List<FileData> dataList = new ArrayList<>();

    @FXML
    private ListView<FileData> listTranslationFiles = new ListView<>();
    private List<FileData> dataListTranslation = new ArrayList<>();

    @FXML
    private Button btnReadLanguage;

    @FXML
    private Button btnStartTranslation;

    @FXML
    private Label lblTask;

    @FXML
    private TextArea txtContentTranslation;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnStartTranslation.setDisable(true);
        txtContentTranslation.setEditable(false);
        lblTask.setText("Task for translation");

        schedServ = new ScheduledService<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        final boolean terminated = executorService.isTerminated();
                        return terminated;
                    }
                };
            }
        };

        schedServ.setDelay(Duration.millis(50)); // Will start after 0.5s
        schedServ.setPeriod(Duration.millis(50)); // Runs every second after

        schedServ.setOnSucceeded(e -> {
            if (!Objects.isNull(schedServ.getValue()) && schedServ.getValue()) {
                // Executor finished
                schedServ.cancel(); // Cancel service (stop it).
                btnStartTranslation.setDisable(false);
                btnReadLanguage.setDisable(false);
                MessageUtils.showMessage("","The files have been translated");
            }
            if (!Objects.isNull(executorService))
                lblTask.setText(executorService.getCompletedTaskCount() + " of " +
                        executorService.getTaskCount() + " task finished");
        });

    }

    public void loadLanguages() {
        dictionaries = FileUtils.readLanguages(Paths.get(DIRECTORY_ORIGINAL.concat(FILE_LANGUAGE)));
        if(!dictionaries.isEmpty()){
            btnStartTranslation.setDisable(false);
        }
    }

    public void loadFilesTranslation(Event e) {
        txtContentTranslation.clear();
        listTranslationFiles.getItems().clear();

        String selectedItems = listOriginalFileData.getSelectionModel().getSelectedItems().get(0).toString();
        String directory = selectedItems.substring(0, selectedItems.indexOf("."));

        dataListTranslation = FileUtils.getListaFileData(Paths.get(DIRECTORY_TRANSLATION.concat(directory)));
        listTranslationFiles.getItems().setAll(dataListTranslation);
    }

    public void loadContentFileTranslation(Event e) {
        FileData selectedItems = listTranslationFiles.getSelectionModel().getSelectedItems().get(0);
        String pathComplete = selectedItems.getFilePath()
                .concat(selectedItems.getFileName());
        try {
            txtContentTranslation.setText(FileUtils.readFile(Paths.get(pathComplete)));
        } catch (IOException ex) {
            MessageUtils.log(MessageUtils.MessageType.ERROR, ex.getMessage());
        }
    }


    public void startTranslation() {
        resetViews();

        loadListFileOrigin();

        List<Runnable> collect = dataList.stream().map(l -> crearTraduccion(l))
                .collect(Collectors.toList());

        executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        collect.stream().forEach(h -> executorService.submit(h));

        executorService.shutdown();
    }

    private void resetViews() {
        listOriginalFileData.getItems().clear();
        listTranslationFiles.getItems().clear();
        txtContentTranslation.clear();

        btnStartTranslation.setDisable(true);
        btnReadLanguage.setDisable(true);
        schedServ.restart();
    }

    public void loadChart() {
    }

    private void loadListFileOrigin() {
        dataList = FileUtils.getListaFileData(Paths.get(ORIGINAL_FILES));
    }

    private Runnable crearTraduccion(FileData fileData) {
        return () -> {
            System.out.println("Entrando en crearTraduccion ");
            String nameFile = fileData.getFileName().split("\\.")[0];

            System.out.println("Nombre del fichero a traducir " + nameFile);

            // TODO: Busque una subcarpeta (dentro de la carpeta de imágenes) con
            //  el mismo nombre que su archivo asociado. Por ejemplo, si el nombre
            //  del archivo es exampleSP.txt, entonces debe buscar una carpeta llamada
            //  exampleSP. Si la carpeta existe, debe eliminarse (utilizando el método FileUtils.deleteDirectory)
            FileUtils.getListaFileData(Paths.get(DIRECTORY_TRANSLATION)).stream().forEach(
                    d -> {
                        if (d.getFileName().equalsIgnoreCase(nameFile)) {
                            try {
                                FileUtils.deleteDirectory(Paths.get(DIRECTORY_TRANSLATION.concat(nameFile)));
                            } catch (IOException e) {
                                MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());
                            }
                        }
                    }
            );

            // TODO: Cree, nuevamente, la carpeta previamente eliminada, para que esté vacía ahora
            //  (puede usar el método Files.createDirectory para esto)

            try {
                Path path = Paths.get(DIRECTORY_TRANSLATION.concat(nameFile));
                Files.createDirectory(path, new FileAttribute[]{});
            } catch (IOException e) {
                MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());


            }

            // TODO: Detectar el idioma original del archivo actual: puedes leer la primera línea del archivo
            //  y buscarlo en los diferentes idiomas de la lista, cuando lo encontremos tendremos disponible
            //  el objeto Idioma para hacer la traducción.
            AtomicReference<String> firstLineToTranslater = new AtomicReference<>("");
            List<String> completeListSentenceToTranslate = new ArrayList<>();
            AtomicReference<Boolean> primer = new AtomicReference<>(true);
            Path path = Paths.get(ORIGINAL_FILES.concat(nameFile.concat(".txt")));

            try (Stream<String> stream = Files.lines(path)) {
                stream.forEach(line -> {
                    if (primer.get()) {
                        firstLineToTranslater.set(line.trim().toLowerCase());
                        primer.set(false);
                    }
                    completeListSentenceToTranslate.add(line);
                });
            } catch (IOException e) {
                MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());

            }

            List<Language> diccionariesFromTranslater = dictionaries.stream()
                    .filter(dictionary -> {
                        String to = dictionary.getTo();
                        String from = dictionary.getFrom();
                        String purgeKey = firstLineToTranslater.get().trim().toLowerCase();
                        if (purgeKey.contains(".")) {
                            purgeKey = purgeKey.substring(0, purgeKey.indexOf("."));
                        }
                        boolean isInDictionary = dictionary.getCurrentLanguage(purgeKey);
                        return isInDictionary;
                    })
                    .collect(Collectors.toList());

            // TODO: Leer todo el archivo y traducir línea por línea, escribiendo el resultado en un nuevo
            //  archivo llamado: language.to + ”_” + nombre del archivo original.

            diccionariesFromTranslater.stream().forEach(dictionary -> {
                System.out.println("Traducciendo desde el idioma " + dictionary.getFrom());
                System.out.println("Traducciendo al  idioma " + dictionary.getTo());

                try {
                    String ruta = DIRECTORY_TRANSLATION.concat(nameFile)
                            .concat("/")
                            .concat(dictionary.getTo())
                            .concat("_")
                            .concat(nameFile)
                            .concat(".txt");

                    File file = new File(ruta);

                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fw = new FileWriter(file);
                    Writer bw = new BufferedWriter(fw);

                    completeListSentenceToTranslate.stream().forEach(lineTo -> {
                        String sententeToTranslate = lineTo.trim().toLowerCase();
                        if (sententeToTranslate.contains(".")) {
                            sententeToTranslate = sententeToTranslate.substring(0, sententeToTranslate.indexOf(".") );
                        }
                        String translatedSentece = dictionary.getTraduction(sententeToTranslate);
                        try {
                            if (Objects.isNull(translatedSentece)) {
                                // MessageUtils.log(MessageUtils.MessageType.ERROR, "Not found translation ".concat(translatedSentece));
                            }
                            if (!Objects.isNull(translatedSentece)) {
                                bw.write(translatedSentece + "\n");
                            }
                        } catch (IOException e) {
                            MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());
                        }
                    });
                    bw.close();
                } catch (Exception e) {
                    MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());
                }
            });

            // TODO: Una vez finalizados todos estos procesos de traducción, el hilo debe usar el método
            //  Platform.runLater para agregar el nombre de archivo original del archivo a la vista de lista
            //  de la izquierda

            Platform.runLater(() -> { listOriginalFileData.getItems().add(fileData); });
        };
    }
}