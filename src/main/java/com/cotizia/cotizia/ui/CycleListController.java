package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.services.CycleService;
import com.cotizia.cotizia.models.Cycle;
import com.cotizia.cotizia.services.AuthenticationService;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class CycleListController {

    @FXML
    private ListView<Cycle> cycleListView;

    private CycleService cycleService = new CycleService();
    private ObservableList<Cycle> cycleObservableList;
    private AuthenticationService authService = new AuthenticationService();

    @FXML
    public void initialize() {
        if (authService.isCollecteur()) {
            int collecteurId = AuthenticationService.getCurrentUser() != null
                    ? AuthenticationService.getCurrentUser().getId()
                    : 1;
            List<Cycle> cycles = cycleService.getCyclesForCollecteur(collecteurId);
            cycleObservableList = FXCollections.observableArrayList(cycles);
            cycleListView.setItems(cycleObservableList);
        }

        // Replace lambda with anonymous inner class for maximum compatibility
        cycleListView.setCellFactory(
                new javafx.util.Callback<javafx.scene.control.ListView<Cycle>, javafx.scene.control.ListCell<Cycle>>() {
                    public javafx.scene.control.ListCell<Cycle> call(javafx.scene.control.ListView<Cycle> param) {
                        return new javafx.scene.control.ListCell<Cycle>() {
                            protected void updateItem(Cycle item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                } else {
                                    setText(item.getNom() + " - "
                                            + String.format("%.0f FG", item.getMontantCotisation()) + " ("
                                            + item.getFrequence() + ")");
                                }
                            }
                        };
                    }
                });
    }

    @FXML
    private void handleNewCycle() {
        try {
            Cotizia.setRoot("cycle_form");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageParticipants() {
        if ("ADHERANT".equalsIgnoreCase(AuthenticationService.getCurrentUser().getRole())) {
            return; // Adherants should not be managing participants
        }
        Cycle selected = cycleListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            com.cotizia.cotizia.utils.SelectionContext.getInstance().setSelectedCycle(selected);
            try {
                Cotizia.setRoot("participant_list");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun cycle sélectionné");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Cotizia.setRoot("dashboard");
    }
}
