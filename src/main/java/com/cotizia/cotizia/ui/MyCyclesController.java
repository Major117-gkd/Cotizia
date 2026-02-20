package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.implementation.ParticipantDAO;
import com.cotizia.cotizia.models.Cycle;
import com.cotizia.cotizia.models.Participant;
import com.cotizia.cotizia.services.AuthenticationService;
import com.cotizia.cotizia.utils.SelectionContext;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class MyCyclesController {

    @FXML
    private ListView<Participant> cyclesListView;
    @FXML
    private Label statusLabel;

    private ParticipantDAO participantDAO = new ParticipantDAO();

    @FXML
    public void initialize() {
        int userId = AuthenticationService.getCurrentUser().getId();
        List<Participant> participations = participantDAO.findByUtilisateur(userId);
        cyclesListView.setItems(FXCollections.observableArrayList(participations));

        // Replace lambda with anonymous inner class for compatibility
        cyclesListView.setCellFactory(
                new javafx.util.Callback<javafx.scene.control.ListView<Participant>, javafx.scene.control.ListCell<Participant>>() {
                    public javafx.scene.control.ListCell<Participant> call(
                            javafx.scene.control.ListView<Participant> param) {
                        return new javafx.scene.control.ListCell<Participant>() {
                            protected void updateItem(Participant item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null || item.getCycle() == null) {
                                    setText(null);
                                } else {
                                    Cycle c = item.getCycle();
                                    setText(c.getNom() + " - " + String.format("%.0f FG", c.getMontantCotisation())
                                            + " ("
                                            + c.getFrequence() + ")");
                                }
                            }
                        };
                    }
                });
    }

    @FXML
    private void handleDetails() {
        Participant selected = cyclesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SelectionContext.getInstance().setSelectedParticipant(selected);
            if (selected.getCycle() != null) {
                SelectionContext.getInstance().setSelectedCycle(selected.getCycle());
            }
            try {
                // Reuse payment view but might need adjustment if Adherant view is read-only
                // for payment status?
                // Or maybe they can see their deadlines.
                // PaymentController allows marking as PAID, which might differ for Adherant
                // (maybe they can initiate payment)
                // For now, let's just let them see. We might need to handle permission in
                // PaymentController.
                Cotizia.setRoot("payment_view");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Veuillez sélectionner un cycle.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Cotizia.setRoot("dashboard");
    }
}
