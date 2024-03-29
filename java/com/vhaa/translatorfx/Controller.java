package com.vhaa.translatorfx;

import com.vhaa.translatorfx.utils.*;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class Main Controller
 * <p>
 * Controller that manages the processes of reading the dictionary files, the original files, executing the translations of the same
 *
 * @author Victor Hugo Aguilar Aguilar
 */
public class Controller implements Initializable {
    private static final String DIRECTORY_ORIGINAL = "./dictionaries/";
    private static final String FILE_LANGUAGE = "languages.txt";
    private static final String ORIGINAL_FILES = "./originalFiles/";
    private static final String DIRECTORY_TRANSLATION = "./translations/";

    private ThreadPoolExecutor executorService;
    private ScheduledService<Boolean> scheduledService;

    private static List<Language> dictionaries = new ArrayList<>();

    @FXML
    private ListView<FileData> listOriginalFileData = new ListView<>();
    private List<FileData> listOriginalFiles = new ArrayList<>();

    @FXML
    private ListView<FileData> listTranslationFiles = new ListView<>();
    private List<FileData> listFilesTranslated = new ArrayList<>();

    @FXML
    private Button btnReadLanguage;

    @FXML
    private Button btnStartTranslation;

    @FXML
    private Label lblTask;

    @FXML
    private TextArea txtContentTranslation;

    /**
     * Initializer method, in which we create the scheduled service that we will then
     * initialize when we execute the translation of the files
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnStartTranslation.setDisable(true);
        txtContentTranslation.setEditable(false);
        lblTask.setText("Task for translation");

        scheduledService = new ScheduledService< >() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task< >() {
                    @Override
                    protected Boolean call() {
                        return executorService.isTerminated();
                    }
                };
            }
        };

        scheduledService.setDelay(Duration.millis(50));
        scheduledService.setPeriod(Duration.millis(50));

        scheduledService.setOnSucceeded(e -> {
            if (!Objects.isNull(scheduledService.getValue()) && scheduledService.getValue()) {
                scheduledService.cancel();
                btnStartTranslation.setDisable(false);
                btnReadLanguage.setDisable(false);
                MessageUtils.showMessage("", "The files have been translated");
            }
            if (!Objects.isNull(executorService))
                lblTask.setText(executorService.getCompletedTaskCount() + " of " +
                        executorService.getTaskCount() + " task finished");
        });
    }

    /**
     * Dictionary loading method, read the languages of the same and leave it in a variable
     */
    public void loadLanguages() {
        try {
            dictionaries = FileUtils.readLanguages(Paths.get(DIRECTORY_ORIGINAL.concat(FILE_LANGUAGE)));
        } catch (IOException e) {
            MessageUtils.showError("", "Dictionary could not be loaded");
        }
        if (!dictionaries.isEmpty()) {
            btnStartTranslation.setDisable(false);
            MessageUtils.showMessage("", "Dictionary loaded correctly");
        }
    }

    /**
     * Method that reads the translated files to store them in the list on the right
     */
    public void loadFilesTranslation() {
        if (listOriginalFiles.isEmpty() ||
                listOriginalFileData.getSelectionModel().getSelectedItems().isEmpty()) {
            return;
        }

        txtContentTranslation.clear();
        listTranslationFiles.getItems().clear();

        String selectedItems = listOriginalFileData.getSelectionModel().getSelectedItems().get(0).toString();
        String directory = selectedItems.substring(0, selectedItems.indexOf("."));

        try {
            listFilesTranslated = FileUtils.getListaFileData(Paths.get(DIRECTORY_TRANSLATION.concat(directory)));
        } catch (IOException ex) {
            MessageUtils.showError("", "The files could not be translated due to ".concat(ex.getMessage()));
            return;
        }

        listTranslationFiles.getItems().setAll(listFilesTranslated);
    }

    /**
     * Reading method of the translated files, and we show it in the visa center
     */
    public void loadContentFileTranslation() {
        if (listFilesTranslated.isEmpty() ||
                listTranslationFiles.getSelectionModel().getSelectedItems().isEmpty()) {
            return;
        }

        FileData selectedItems = listTranslationFiles.getSelectionModel().getSelectedItems().get(0);
        String pathComplete = selectedItems.getFilePath()
                .concat(selectedItems.getFileName());
        try {
            txtContentTranslation.setText(FileUtils.readFile(Paths.get(pathComplete)));
        } catch (IOException ex) {
            MessageUtils.log(MessageUtils.MessageType.ERROR, ex.getMessage());
        }
    }

    /**
     * Method that we launched to create the translations of the original files
     */
    public void startTranslation() {
        resetViews();
        resetCharts();

        loadListFileOrigin();

        List<Runnable> collect = listOriginalFiles.stream().map(this::createTranslation)
                .collect(Collectors.toList());
        executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        collect.forEach(h -> executorService.submit(h));
        executorService.shutdown();
    }

    /**
     * Method that reads the original files and loads them in the variable
     */
    private void loadListFileOrigin() {
        try {
            listOriginalFiles = FileUtils.getListaFileData(Paths.get(ORIGINAL_FILES));
        } catch (IOException e) {
            MessageUtils.showError("", "Error in reading the original files ");
        }
    }

    /**
     * Method that launches a thread for the execution of the complete process for each file that we have.
     *
     * @return Runnable
     */
    private Runnable createTranslation(FileData fileData) {
        return () -> {
            long initProcess = System.currentTimeMillis();
            String nameFile = fileData.getFileName().split("\\.")[0];
            MessageUtils.log(MessageUtils.MessageType.INFO, "File to translate ".concat(nameFile));

            /*
              Look for a subfolder (inside images folder) with the same name as its associated file. For instance,
              if the file name is exampleSP.txt, then it must look for a folder called exampleSP. If the folder
              exists, it must be deleted (using FileUtils.deleteDirectory method)
             */
            try {
                FileUtils.getListaFileData(Paths.get(DIRECTORY_TRANSLATION)).forEach(
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
            } catch (IOException e) {
                MessageUtils.log(MessageUtils.MessageType.ERROR, "Error in reading the file ".concat(e.getMessage()));
            }

            /*
               Create, again, the folder previously deleted, so that it is empty now (you can use
               Files.createDirectory method for this)
             */
            try {
                Path path = Paths.get(DIRECTORY_TRANSLATION.concat(nameFile));
                Files.createDirectory(path);
            } catch (IOException e) {
                MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());
            }

            /*
              Detect the original language of the current file:you can read the first line of the file and look for
              it in the different languages of the list, when we find it we will have the Language object available
              to do the translation.
             */
            AtomicReference<String> firstLineToTranslate = new AtomicReference<>("");
            List<String> completeListSentenceToTranslate = new ArrayList<>();
            AtomicReference<Boolean> primer = new AtomicReference<>(true);
            Path path = Paths.get(ORIGINAL_FILES.concat(nameFile.concat(".txt")));

            try (Stream<String> stream = Files.lines(path)) {
                stream.forEach(line -> {
                    if (primer.get()) {
                        firstLineToTranslate.set(line.trim().toLowerCase());
                        primer.set(false);
                    }
                    completeListSentenceToTranslate.add(line);
                });
            } catch (IOException e) {
                MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());

            }

            List<Language> languagesToTranslate = dictionaries.stream()
                    .filter(dictionary -> {
                        String purgeKey = firstLineToTranslate.get().trim().toLowerCase();
                        if (purgeKey.contains(".")) {
                            purgeKey = purgeKey.substring(0, purgeKey.indexOf("."));
                        }
                        return dictionary.getCurrentLanguage(purgeKey);
                    })
                    .collect(Collectors.toList());

            /*
              Read all the file and translate line by line, writing the result in a new file called:
              language.to + ” _ ” + original file name.
             */
            languagesToTranslate.forEach(dictionary -> {
                MessageUtils.log(MessageUtils.MessageType.INFO, "Translating from ".concat(dictionary.getFrom()));
                MessageUtils.log(MessageUtils.MessageType.INFO, "Translating to ".concat(dictionary.getTo()));
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

                    completeListSentenceToTranslate.forEach(lineTo -> {
                        String sentenceToTranslate = lineTo.trim().toLowerCase();
                        if (sentenceToTranslate.contains(".")) {
                            sentenceToTranslate = sentenceToTranslate.substring(0, sentenceToTranslate.indexOf("."));
                        }
                        String translatedSentence = dictionary.getTraduction(sentenceToTranslate);
                        try {
                            if (Objects.isNull(translatedSentence)) {
                                //MessageUtils.log(MessageUtils.MessageType.ERROR, "Not translation found for -> ".concat(sentenceToTranslate));
                                return;
                            }
                            bw.write(translatedSentence + "\n");
                        } catch (IOException e) {
                            MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());
                        }
                    });
                    bw.close();
                } catch (Exception e) {
                    MessageUtils.log(MessageUtils.MessageType.ERROR, e.getMessage());
                }
            });

            /*
              After all these translation processes have finished, the thread must use Platform.runLater method to
              add file original filename to the left list view
             */
            Platform.runLater(() -> {
                long endProcess = System.currentTimeMillis();
                double timeProcess = (double) ((endProcess - initProcess));
                FileTimeProcess fileTimeProcess = new FileTimeProcess(fileData.getFileName(), timeProcess);
                FilesTimes.getInstance().addFileTime(fileTimeProcess);
                MessageUtils.log(MessageUtils.MessageType.INFO, "File ".concat(fileData.getFileName()).concat(" correctly translated"));
                listOriginalFileData.getItems().add(fileData);
            });
        };

    }

    /**
     * Method that resets the values and clears the views
     */
    private void resetViews() {
        listOriginalFileData.getItems().clear();
        listTranslationFiles.getItems().clear();
        txtContentTranslation.clear();

        btnStartTranslation.setDisable(true);
        btnReadLanguage.setDisable(true);
        scheduledService.restart();
    }

    /**
     * Method for reset static memory chart
     */
    private void resetCharts() {
        FilesTimes.getInstance().clearList();
    }

    /**
     * Method to launch the modal view of the graphs
     *
     */
    public void loadChart(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/vhaa/translatorfx/chart-view.fxml")));
            Scene chartScene = new Scene(root, 600, 400);
            chartScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("main-View.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("TranslatorFX");
            stage.setScene(chartScene);
            // Create Modal
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(
                    ((Node) event.getSource()).getScene().getWindow());
            // Show Stage
            stage.show();
        } catch (IOException e) {
            MessageUtils.log(MessageUtils.MessageType.ERROR, e.toString());
        }
    }
}