package io.gitlab.swded.system.controller;

import com.sun.javafx.collections.ObservableListWrapper;
import io.gitlab.swded.system.model.DataRow;
import io.gitlab.swded.system.model.processing.Classifier;
import io.gitlab.swded.system.model.processing.Metric;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ClassificationInputController extends ChartInputController {

    @FXML
    private ComboBox<Integer> numberComboBox;
    @FXML
    private ComboBox<Metric> metricComboBox;
    @FXML
    private TextField inputTextField;
    private List<DataRow> data;

    public void setData(List<DataRow> data) {
        this.data = data;
    }

    public void confirm(ActionEvent actionEvent) {
        if (!validateInput()) {
            return;
        }
        DataRow unknownObject = parseInputRow();
        Classifier classifier = createClassifier();
        String aClass = classifier.classify(unknownObject, numberComboBox.getValue(), metricComboBox.getValue());
        unknownObject.getValues().get(getClassColumnIndex()).setText(aClass);
        printOutput(aClass);
    }

    private DataRow parseInputRow() {
        String input = inputTextField.getText();
        DataRow inputObject = new DataRow(input.split(" "));
        DataRow unknownObject = new DataRow(data.get(0).toString().split(" "));
        unknownObject.getValues().forEach(value -> {
            if (value.isNumber()) {
                value.setValue(-Double.MAX_VALUE);
            } else {
                value.setText("UNKNOWN");
            }
        });
        int[] valueColumnIndexes = getValueColumnIndexes();
        for (int i = 0; i < valueColumnIndexes.length; i++) {
            unknownObject.getValues().get(valueColumnIndexes[i]).setValue(inputObject.getNumericValue(i));
        }
        int classColumnIndex = getClassColumnIndex();
        unknownObject.getValues().get(classColumnIndex).setText("UNCLASSIFIED");
        return unknownObject;
    }

    private Classifier createClassifier() {
        Classifier classifier = new Classifier(data);
        classifier.setClassColumnIndex(getClassColumnIndex());
        classifier.setValueColumnIndexes(getValueColumnIndexes());
        return classifier;
    }

    private void printOutput(String aClass) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "The object has been classified as: " + aClass);
        infoAlert.setHeaderText(null);
        infoAlert.setTitle("Classification output");
        infoAlert.show();
    }

    @Override
    boolean validateInput() {
        return validateSelectedClassColumns();
    }

    public void close(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getTarget()).getScene().getWindow()).close();
    }

    public void initializeUI(DataRow defaultDataRow, List<String> header) {
        super.initializeUI(defaultDataRow, header);
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            numbers.add(i);
        }
        numberComboBox.setItems(new ObservableListWrapper<>(numbers));
        numberComboBox.getSelectionModel().selectFirst();
        metricComboBox.setItems(FXCollections.observableArrayList(Metric.values()));
        metricComboBox.getSelectionModel().selectFirst();
    }

}