package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.implementation.UtilisateurDAO;
import com.cotizia.cotizia.models.Utilisateur;
import com.cotizia.cotizia.services.AuthenticationService;
import com.cotizia.cotizia.utils.SelectionContext;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class UserListController {

    @FXML
    private TableView<Utilisateur> userTableView;
    @FXML
    private Label statusLabel;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private com.cotizia.cotizia.implementation.MouchardDAO mouchardDAO = new com.cotizia.cotizia.implementation.MouchardDAO();

    @FXML
    public void initialize() {
        if (!isAdmin()) {
            statusLabel.setText("Accès restreint aux administrateurs.");
            userTableView.setPlaceholder(new Label("Accès refusé."));
            return;
        }
        refreshList();
    }

    private boolean isAdmin() {
        Utilisateur current = AuthenticationService.getCurrentUser();
        return current != null && "ADMIN".equalsIgnoreCase(current.getRole());
    }

    private void refreshList() {
        List<Utilisateur> users = utilisateurDAO.findAll();
        ObservableList<Utilisateur> items = FXCollections.observableArrayList(users);
        userTableView.setItems(items);
    }

    @FXML
    private void handleAddUser() {
        if (!isAdmin())
            return;
        try {
            SelectionContext.getInstance().setSelectedUser(null); // Clear selection for new user
            Cotizia.setRoot("user_form");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditUser() {
        if (!isAdmin())
            return;
        Utilisateur selected = userTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SelectionContext.getInstance().setSelectedUser(selected);
            try {
                Cotizia.setRoot("user_form");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Sélectionnez un utilisateur.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        if (!isAdmin())
            return;
        Utilisateur selected = userTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Empêcher de se supprimer soi-même
            if (selected.getId() == AuthenticationService.getCurrentUser().getId()) {
                statusLabel.setText("Impossible de supprimer votre propre compte.");
                return;
            }

            // Boîte de dialogue de confirmation personnalisée
            javafx.scene.control.ButtonType acceptBtn = new javafx.scene.control.ButtonType("Accepter",
                    javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType refuseBtn = new javafx.scene.control.ButtonType("Refuser",
                    javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.NONE, // Utilisation de NONE pour un contrôle total sans icône
                                                               // par défaut
                    "Souhaitez-vous vraiment supprimer l'utilisateur\n" + selected.getNom() + " " + selected.getPrenom()
                            + " ?\n\nCette action est irréversible et supprimera toutes les données associées.",
                    acceptBtn, refuseBtn);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Confirmation de suppression");

            // Appliquer les styles CSS
            javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStyleClass().add("custom-alert");
            dialogPane.setGraphic(null); // Retire l'icône par défaut pour un look épuré

            // On s'assure que le fichier CSS est bien chargé (il l'est normalement via
            // Cotizia,
            // mais les Dialogs ont parfois besoin d'une injection explicite dans leur
            // propre scène)
            String stylesheet = getClass().getResource("/com/cotizia/cotizia/ui/style.css").toExternalForm();
            dialogPane.getStylesheets().add(stylesheet);

            // Ajuster les boutons
            javafx.scene.control.Button acceptButton = (javafx.scene.control.Button) dialogPane.lookupButton(acceptBtn);
            acceptButton.getStyleClass().add("button-danger");

            javafx.scene.control.Button refuseButton = (javafx.scene.control.Button) dialogPane.lookupButton(refuseBtn);
            refuseButton.getStyleClass().add("button-neutral");

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == acceptBtn) {
                utilisateurDAO.delete(selected.getId());
                mouchardDAO.log("Suppression utilisateur: " + selected.getEmail(),
                        AuthenticationService.getCurrentUser().getId());
                statusLabel.setText("Utilisateur supprimé avec succès.");
                refreshList();
            } else {
                statusLabel.setText("Suppression annulée.");
            }
        } else {
            statusLabel.setText("Sélectionnez un utilisateur.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Cotizia.setRoot("dashboard");
    }
}
