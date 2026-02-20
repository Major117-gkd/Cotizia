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
            utilisateurDAO.delete(selected.getId());
            mouchardDAO.log("Suppression utilisateur: " + selected.getEmail(),
                    AuthenticationService.getCurrentUser().getId());
            statusLabel.setText("Utilisateur supprimé.");
            refreshList();
        } else {
            statusLabel.setText("Sélectionnez un utilisateur.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Cotizia.setRoot("dashboard");
    }
}
