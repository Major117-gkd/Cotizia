package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.models.Collecteur;
import com.cotizia.cotizia.models.Cycle;
import com.cotizia.cotizia.models.Utilisateur;
import com.cotizia.cotizia.services.AuthenticationService;
import com.cotizia.cotizia.services.CycleService;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CycleFormController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField montantField;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private ComboBox<String> frequenceBox;
    @FXML
    private Label errorLabel;

    private CycleService cycleService = new CycleService();
    private com.cotizia.cotizia.implementation.MouchardDAO mouchardDAO = new com.cotizia.cotizia.implementation.MouchardDAO();

    @FXML
    private void handleCreate() {
        try {
            Cycle cycle = new Cycle();
            cycle.setNom(nomField.getText());
            cycle.setMontantCotisation(Double.parseDouble(montantField.getText()));
            cycle.setDateDebut(dateDebutPicker.getValue());
            cycle.setFrequence(frequenceBox.getValue());
            cycle.setEtat("EN_COURS");

            Utilisateur currentUser = AuthenticationService.getCurrentUser();
            if (currentUser instanceof Collecteur) {
                cycle.setCollecteur((Collecteur) currentUser);
            } else {
                errorLabel.setText("Erreur: Vous n'êtes pas collecteur.");
                return;
            }

            cycleService.creerCycle(cycle);

            // Log action for Admin
            mouchardDAO.log("Création du cycle: " + cycle.getNom(), currentUser.getId());

            Cotizia.setRoot("dashboard");

        } catch (NumberFormatException e) {
            errorLabel.setText("Montant invalide.");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la création.");
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        Cotizia.setRoot("dashboard");
    }
}
