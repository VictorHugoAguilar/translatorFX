package com.vhaa.translatorfx.chart;

import com.vhaa.translatorfx.utils.FileTimeProcess;
import com.vhaa.translatorfx.utils.FilesTimes;
import com.vhaa.translatorfx.utils.MessageUtils;
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
        if(listOfProcess.isEmpty()){
            MessageUtils.log(MessageUtils.MessageType.ERROR, "Values of chart not loaded correctly");
            return;
        }

        XYChart.Series data = new XYChart.Series();
        data.setName("Process duration in milliseconds");
        for (FileTimeProcess process : listOfProcess) {
            data.getData().add(new XYChart.Data(process.getFile(), process.getTime()));
        }

        if(barChart.getData().add(data)){
            MessageUtils.log(MessageUtils.MessageType.INFO, "Values of chart loaded correctly");
        }
    }

    /**
     * Method to return to the main page
     */
    @FXML
    protected void goToMain() {
        Stage stage = (Stage) this.btnBackToMainChart.getScene().getWindow();
        stage.close();
    }
}
