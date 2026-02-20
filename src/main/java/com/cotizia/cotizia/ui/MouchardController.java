package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.implementation.MouchardDAO;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class MouchardController {

    @FXML
    private ListView<String> logListView;

    private MouchardDAO mouchardDAO = new MouchardDAO();

    @FXML
    public void initialize() {
        if (!"ADMIN".equalsIgnoreCase(com.cotizia.cotizia.services.AuthenticationService.getCurrentUser().getRole())) {
            System.err.println("MouchardController: Unauthorized access attempt!");
            logListView.setItems(FXCollections.observableArrayList("Accès refusé : Droits insuffisants."));
            return;
        }
        refreshList();
    }

    private void refreshList() {
        List<String> logs = mouchardDAO.findAllLogsWithUserNames();
        logListView.setItems(FXCollections.observableArrayList(logs));
    }

    @FXML
    private void handleBack() throws IOException {
        Cotizia.setRoot("dashboard");
    }
}
