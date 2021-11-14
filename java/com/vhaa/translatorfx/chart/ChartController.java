package com.vhaa.translatorfx.chart;

import com.vhaa.translatorfx.utils.FileTimeProcess;
import com.vhaa.translatorfx.utils.FilesTimes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class Controller to View Charts
 *
 * @author Victor Hugo Aguilar Aguilar
 */
public class ChartController implements Initializable {

    @FXML
    private Button btnBackToMainChart;

    @FXML
    private BarChart<String, Number> barChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadProcessList();
    }

    /**
     * Method to load list saved of process translation
     */
    private void loadProcessList() {
        List<FileTimeProcess> listOfProcess = FilesTimes.getInstance().getList();
        XYChart.Series data = new XYChart.Series();
        listOfProcess.forEach(process -> {
            data.getData().add(new XYChart.Data(process.getFile(), process.getTime()));
        });
        data.setName("Process duration in milliseconds");
        barChart.getData().add(data);
    }

    /**
     * Method to return to the main page
     */
    @FXML
    public void goToMain(ActionEvent event) {
        /*
        Parent root = FXMLLoader.load(getClass().getResource("/com/vhaa/translatorrfx/main-view.fxml"));
        Scene mainScene = new Scene(root);
        mainScene.getRoot().setStyle("-fx-font-family: 'Verdana'");
        mainScene.getStylesheets().add(getClass().getResource("/com/vhaa/translatorrfx/main-view.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(mainScene);
        stage.show();
        */
        Stage stage = (Stage) this.btnBackToMainChart.getScene().getWindow();
        stage.close();
    }
}
