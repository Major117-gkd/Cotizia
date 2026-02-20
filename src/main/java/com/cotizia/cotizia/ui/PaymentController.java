package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.implementation.EcheanceDAO;
import com.cotizia.cotizia.models.Echeance;
import com.cotizia.cotizia.models.Participant;
import com.cotizia.cotizia.utils.SelectionContext;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;

public class PaymentController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label participantLabel;
    @FXML
    private ListView<Echeance> echeanceListView;
    @FXML
    private Label statusLabel;
    @FXML
    private javafx.scene.control.Button payButton;

    private EcheanceDAO echeanceDAO = new EcheanceDAO();
    private com.cotizia.cotizia.implementation.MouchardDAO mouchardDAO = new com.cotizia.cotizia.implementation.MouchardDAO();
    private Participant currentParticipant;

    @FXML
    public void initialize() {
        System.out.println("PaymentController: initialize() STARTED");
        try {
            currentParticipant = SelectionContext.getInstance().getSelectedParticipant();
            if (currentParticipant != null) {
                System.out.println(
                        "PaymentController: Found selected participant (ID: " + currentParticipant.getId() + ")");

                String displayName = "N/A";
                if (currentParticipant.getUtilisateur() != null) {
                    displayName = currentParticipant.getUtilisateur().getNom() + " "
                            + currentParticipant.getUtilisateur().getPrenom();
                }
                participantLabel.setText("Participant: " + displayName);
                refreshList();
            } else {
                System.err.println("PaymentController: currentParticipant is NULL!");
                statusLabel.setText("Aucun participant sélectionné.");
            }

            // Custom cell factory to display Echeance details nicely
            echeanceListView.setCellFactory(
                    new javafx.util.Callback<javafx.scene.control.ListView<Echeance>, javafx.scene.control.ListCell<Echeance>>() {
                        public javafx.scene.control.ListCell<Echeance> call(
                                javafx.scene.control.ListView<Echeance> param) {
                            return new ListCell<Echeance>() {
                                protected void updateItem(Echeance item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty || item == null) {
                                        setText(null);
                                        setStyle("");
                                    } else {
                                        String status = (item.getStatut() != null) ? item.getStatut() : "INCONNU";
                                        String date = (item.getDatePrevue() != null) ? item.getDatePrevue().toString()
                                                : "N/A";
                                        String payDate = item.getDatePaiement() != null
                                                ? item.getDatePaiement().toString()
                                                : "-";

                                        setText(date + " | " + status + " | Payé le: " + payDate);

                                        if ("PAYE".equalsIgnoreCase(status)) {
                                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                        } else if ("RETARD".equalsIgnoreCase(status)) {
                                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                        } else {
                                            setStyle("-fx-text-fill: black;");
                                        }
                                    }
                                }
                            };
                        }
                    });

            if ("ADHERANT"
                    .equalsIgnoreCase(com.cotizia.cotizia.services.AuthenticationService.getCurrentUser().getRole())) {
                payButton.setVisible(false);
                payButton.setManaged(false);
            }
        } catch (Exception e) {
            System.err.println("PaymentController: CRITICAL ERROR in initialize():");
            e.printStackTrace();
        }
        System.out.println("PaymentController: initialize() FINISHED");
    }

    private void refreshList() {
        if (currentParticipant == null)
            return;

        System.out.println("PaymentController: refreshList() for participant ID=" + currentParticipant.getId());
        List<Echeance> echeances = echeanceDAO.findByParticipant(currentParticipant.getId());

        // If no echeances found, maybe they were never generated? (e.g. existing data)
        if (echeances.isEmpty()) {
            System.out
                    .println("PaymentController: No echeances found. Attempting to generate them via CycleService...");
            try {
                new com.cotizia.cotizia.services.CycleService().genererEcheances(currentParticipant);
                // Re-fetch after generation
                echeances = echeanceDAO.findByParticipant(currentParticipant.getId());
                System.out.println("PaymentController: Echeances generated and re-fetched. Count=" + echeances.size());
            } catch (Exception e) {
                System.err.println("PaymentController: Failed to auto-generate echeances: " + e.getMessage());
                e.printStackTrace();
            }
        }

        ObservableList<Echeance> items = FXCollections.observableArrayList(echeances);
        echeanceListView.setItems(items);

        if (items.isEmpty()) {
            statusLabel.setText("Aucune échéance disponible.");
        } else {
            statusLabel.setText("Sélectionnez une échéance.");
        }
    }

    @FXML
    private void handleMarkPaid() {
        if ("ADHERANT"
                .equalsIgnoreCase(com.cotizia.cotizia.services.AuthenticationService.getCurrentUser().getRole())) {
            statusLabel.setText("Action non autorisée.");
            return;
        }

        Echeance selected = echeanceListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if ("PAYE".equalsIgnoreCase(selected.getStatut())) {
                statusLabel.setText("Déjà payé !");
                return;
            }

            selected.setStatut("PAYE");
            selected.setDatePaiement(LocalDate.now());
            selected.setMontantPaye(currentParticipant.getCycle().getMontantCotisation()); // Assume full payment

            echeanceDAO.update(selected);

            // Log action for Admin
            mouchardDAO.log("Validation paiement: " + currentParticipant.getUtilisateur().getNom() +
                    " - Date: " + selected.getDatePrevue(),
                    com.cotizia.cotizia.services.AuthenticationService.getCurrentUser().getId());

            statusLabel.setText("Paiement enregistré.");
            refreshList();
        } else {
            statusLabel.setText("Sélectionnez une échéance.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        String role = com.cotizia.cotizia.services.AuthenticationService.getCurrentUser().getRole();
        if ("ADHERANT".equalsIgnoreCase(role)) {
            Cotizia.setRoot("my_cycles");
        } else {
            Cotizia.setRoot("participant_list");
        }
    }
}
