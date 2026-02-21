package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.implementation.UtilisateurDAO;
import com.cotizia.cotizia.models.Cycle;
import com.cotizia.cotizia.models.Participant;
import com.cotizia.cotizia.models.Utilisateur;
import com.cotizia.cotizia.services.CycleService;
import com.cotizia.cotizia.utils.SelectionContext;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.StringConverter;

public class ParticipantListController {

    @FXML
    private Label titleLabel;
    @FXML
    private ListView<Participant> participantListView;
    @FXML
    private ComboBox<Utilisateur> adherentComboBox;
    @FXML
    private Label statusLabel;

    private CycleService cycleService = new CycleService();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private Cycle currentCycle;

    @FXML
    public void initialize() {
        currentCycle = SelectionContext.getInstance().getSelectedCycle();
        if (currentCycle != null) {
            // Security: If collector, verify ownership
            com.cotizia.cotizia.models.Utilisateur user = com.cotizia.cotizia.services.AuthenticationService
                    .getCurrentUser();
            if ("COLLECTEUR".equalsIgnoreCase(user.getRole())) {
                if (currentCycle.getCollecteur() != null && currentCycle.getCollecteur().getId() != user.getId()) {
                    System.err.println("ParticipantListController: SECURITY ALERT: Collector " + user.getId()
                            + " tried to access cycle " + currentCycle.getId() + " owned by "
                            + currentCycle.getCollecteur().getId());
                    statusLabel.setText("Accès refusé : Vous n'êtes pas le gestionnaire de ce cycle.");
                    participantListView.setDisable(true);
                    return;
                }
            }

            System.out.println("ParticipantListController: Initializing for cycle: " + currentCycle.getNom() + " (ID: "
                    + currentCycle.getId() + ")");
            titleLabel.setText("Participants: " + currentCycle.getNom());
            refreshList();
            loadAdherents();
        } else {
            System.err.println("ParticipantListController: No cycle selected in SelectionContext!");
            statusLabel.setText("Aucun cycle sélectionné.");
        }

        participantListView.setCellFactory(
                new javafx.util.Callback<javafx.scene.control.ListView<Participant>, javafx.scene.control.ListCell<Participant>>() {
                    public javafx.scene.control.ListCell<Participant> call(
                            javafx.scene.control.ListView<Participant> param) {
                        return new javafx.scene.control.ListCell<Participant>() {
                            private final javafx.scene.layout.HBox cellBox = new javafx.scene.layout.HBox(10);
                            private final javafx.scene.control.Label nameLabel = new javafx.scene.control.Label();
                            private final javafx.scene.control.Button removeBtn = new javafx.scene.control.Button(
                                    "Retirer");
                            private final javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();

                            {
                                javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                                removeBtn.getStyleClass().add("button-secondary");
                                removeBtn.setStyle(
                                        "-fx-text-fill: #e53935; -fx-border-color: #ef9a9a; -fx-padding: 2 8 2 8;");
                                cellBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                                cellBox.getChildren().addAll(nameLabel, spacer, removeBtn);

                                removeBtn.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                                    public void handle(javafx.event.ActionEvent event) {
                                        Participant p = getItem();
                                        if (p != null) {
                                            handleRemoveParticipant(p);
                                        }
                                    }
                                });
                            }

                            protected void updateItem(Participant item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                    setGraphic(null);
                                } else {
                                    try {
                                        String name = (item.getUtilisateur() != null)
                                                ? item.getUtilisateur().getNom() + " "
                                                        + item.getUtilisateur().getPrenom()
                                                : "Utilisateur Inconnu";
                                        nameLabel.setText(name + " (Pos: " + item.getPositionBeneficiaire() + ")");
                                        setGraphic(cellBox);
                                    } catch (Exception e) {
                                        System.err.println(
                                                "ParticipantListController: Error rendering cell: " + e.getMessage());
                                        nameLabel.setText("Erreur affichage participant");
                                        setGraphic(nameLabel);
                                    }
                                }
                            }
                        };
                    }
                });

        adherentComboBox.setConverter(new StringConverter<Utilisateur>() {
            @Override
            public String toString(Utilisateur user) {
                return (user == null) ? "" : user.getNom() + " " + user.getPrenom() + " (" + user.getEmail() + ")";
            }

            @Override
            public Utilisateur fromString(String string) {
                return null; // Non utilisé
            }
        });
    }

    private void loadAdherents() {
        System.out.println("ParticipantListController: Loading available adherents...");
        List<Utilisateur> adherents = utilisateurDAO.findByRole("ADHERANT");
        System.out.println("ParticipantListController: Found " + adherents.size() + " adherents.");
        adherentComboBox.setItems(FXCollections.observableArrayList(adherents));
    }

    private void refreshList() {
        if (currentCycle == null) {
            System.err.println("ParticipantListController: currentCycle is null, cannot refresh.");
            return;
        }
        final int cycleId = currentCycle.getId();
        System.out.println("ParticipantListController: Refreshing participants for cycle ID: " + cycleId);

        // Use Platform.runLater to ensure UI thread safely updates and triggers redraw
        javafx.application.Platform.runLater(new Runnable() {
            public void run() {
                List<Participant> parts = cycleService.getParticipants(cycleId);
                System.out.println("ParticipantListController: cycleService.getParticipants(" + cycleId + ") returned "
                        + (parts != null ? parts.size() : "null") + " items.");

                if (parts != null) {
                    ObservableList<Participant> items = FXCollections.observableArrayList(parts);
                    participantListView.setItems(null); // Force clear to trigger update
                    participantListView.setItems(items);
                    System.out.println("ParticipantListController: ListView items updated visually.");
                }
                participantListView.getSelectionModel().clearSelection();
            }
        });
    }

    @FXML
    private void handleAddParticipant() {
        Utilisateur selectedUser = adherentComboBox.getSelectionModel().getSelectedItem();
        System.out.println("ParticipantListController: handleAddParticipant called for "
                + (selectedUser != null ? selectedUser.getNom() : "null"));

        if (selectedUser == null) {
            statusLabel.setText("Veuillez sélectionner un adhérent.");
            return;
        }

        if (currentCycle != null) {
            if (currentCycle.getId() == 0) {
                statusLabel.setText("Erreur: ID du cycle invalide (0). Re-sélectionnez le cycle.");
                return;
            }

            try {
                // Check if already in this cycle (Strict database check)
                List<Participant> existing = cycleService.getParticipants(currentCycle.getId());
                if (existing != null) {
                    for (Participant part : existing) {
                        if (part.getUtilisateur().getId() == selectedUser.getId()) {
                            statusLabel.setText("Cet adhérent est déjà participant.");
                            return;
                        }
                    }
                }

                Participant p = new Participant();
                p.setCycle(currentCycle);
                p.setUtilisateur(selectedUser);
                p.setDateInscription(LocalDate.now());
                p.setPositionBeneficiaire((existing != null ? existing.size() : 0) + 1);

                cycleService.ajouterParticipant(currentCycle, p);

                // Track activity for dashboard
                new com.cotizia.cotizia.implementation.MouchardDAO().log(
                        "Ajout de " + selectedUser.getNom() + " au cycle " + currentCycle.getNom(),
                        com.cotizia.cotizia.services.AuthenticationService.getCurrentUser().getId());

                System.out.println("ParticipantListController: Successfully added participant ID " + p.getId());
                statusLabel.setText("Participant ajouté: " + selectedUser.getNom());
                adherentComboBox.getSelectionModel().clearSelection();
                refreshList();
            } catch (Exception e) {
                System.err.println("ParticipantListController: Error adding participant: " + e.getMessage());
                e.printStackTrace();
                statusLabel.setText("Erreur lors de l'ajout.");
            }
        }
    }

    private void handleRemoveParticipant(Participant p) {
        System.out.println(
                "ParticipantListController: handleRemoveParticipant called for " + p.getUtilisateur().getNom());
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Retirer " + p.getUtilisateur().getNom() + " ?");
        alert.setContentText("Cette action supprimera également ses échéances.");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                cycleService.supprimerParticipant(p.getId());

                // Track activity for dashboard
                new com.cotizia.cotizia.implementation.MouchardDAO().log(
                        "Retrait de " + p.getUtilisateur().getNom() + " du cycle " + currentCycle.getNom(),
                        com.cotizia.cotizia.services.AuthenticationService.getCurrentUser().getId());

                statusLabel.setText("Participant retiré.");
                refreshList();
            } catch (Exception e) {
                System.err.println("ParticipantListController: Error removing participant: " + e.getMessage());
                e.printStackTrace();
                statusLabel.setText("Erreur lors du retrait.");
            }
        }
    }

    @FXML
    private void handleViewPayments() {
        Participant selected = participantListView.getSelectionModel().getSelectedItem();
        System.out.println("ParticipantListController: handleViewPayments CLICKED. Selected="
                + (selected != null ? selected.getUtilisateur().getNom() : "NONE"));

        if (selected != null) {
            SelectionContext.getInstance().setSelectedParticipant(selected);
            try {
                System.out.println("ParticipantListController: Attempting Cotizia.setRoot('payment_view')...");
                Cotizia.setRoot("payment_view");
                System.out.println("ParticipantListController: Root set to payment_view successfully.");
            } catch (Exception e) {
                System.err.println("ParticipantListController: CRITICAL ERROR during navigation to payment_view:");
                System.err.println("Error Type: " + e.getClass().getName());
                System.err.println("Error Message: " + e.getMessage());
                e.printStackTrace();
                statusLabel
                        .setText("Erreur navigation: " + (e.getMessage() != null ? e.getMessage() : "Cause inconnue"));
            }
        } else {
            statusLabel.setText("Sélectionnez un participant.");
        }
    }

    @FXML
    private void handleExport() {
        System.out.println("ParticipantListController: handleExport CLICKED.");
        if (currentCycle == null) {
            statusLabel.setText("Aucun cycle sélectionné.");
            return;
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Exporter Participants");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("participants_" + currentCycle.getNom() + ".csv");

        System.out.println("ParticipantListController: Opening FileChooser...");
        javafx.stage.Window window = participantListView.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            System.out.println("ParticipantListController: Exporting to " + file.getAbsolutePath());
            java.io.PrintWriter writer = null;
            try {
                writer = new java.io.PrintWriter(file);
                // Add sep=; to help Excel identify the separator automatically
                writer.println("sep=;");
                writer.println("Nom;Prenom;Email;Date Inscription;Position");
                for (Participant p : participantListView.getItems()) {
                    if (p == null)
                        continue;

                    String nom = (p.getUtilisateur() != null) ? p.getUtilisateur().getNom() : "N/A";
                    String prenom = (p.getUtilisateur() != null) ? p.getUtilisateur().getPrenom() : "N/A";
                    String email = (p.getUtilisateur() != null) ? p.getUtilisateur().getEmail() : "N/A";
                    String dateInsc = (p.getDateInscription() != null) ? p.getDateInscription().toString() : "N/A";

                    writer.printf("%s;%s;%s;%s;%d%n", nom, prenom, email, dateInsc, p.getPositionBeneficiaire());
                }
                statusLabel.setText("Export réussi !");
                System.out.println("ParticipantListController: Export success.");
            } catch (Exception e) {
                System.err.println("ParticipantListController: Export error:");
                e.printStackTrace();
                statusLabel.setText("Erreur export: " + e.getMessage());
            } finally {
                if (writer != null)
                    writer.close();
            }
        } else {
            System.out.println("ParticipantListController: Export cancelled by user.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        String role = com.cotizia.cotizia.services.AuthenticationService.getCurrentUser().getRole();
        if ("ADHERANT".equalsIgnoreCase(role)) {
            Cotizia.setRoot("my_cycles");
        } else {
            Cotizia.setRoot("cycle_list");
        }
    }
}
