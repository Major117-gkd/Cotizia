package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.implementation.CycleDAO;
import com.cotizia.cotizia.implementation.EcheanceDAO;
import com.cotizia.cotizia.implementation.UtilisateurDAO;
import com.cotizia.cotizia.services.AuthenticationService;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private VBox contentArea;
    @FXML
    private VBox recentActivitiesBox;
    @FXML
    private Label totalCyclesLabel;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalCollectedLabel;

    private AuthenticationService authService = new AuthenticationService();
    private CycleDAO cycleDAO = new CycleDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private EcheanceDAO echeanceDAO = new EcheanceDAO();

    @FXML
    private javafx.scene.control.Button cyclesBtn;
    @FXML
    private javafx.scene.control.Button usersBtn;
    @FXML
    private javafx.scene.control.Button mouchardBtn;

    @FXML
    public void initialize() {
        System.out.println("DashboardController: initialize() STARTED");
        try {
            if (!authService.isAuthenticated()) {
                System.out.println("User not authenticated, redirecting to login...");
                try {
                    Cotizia.setRoot("login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("User authenticated: " + AuthenticationService.getCurrentUser().getEmail());
                configureMenuBasedOnRole();
                refreshStats();

                // Setup periodic auto-refresh (every 10 seconds)
                // This is purely functional to keep the dashboard logs/stats live.
                javafx.animation.Timeline refreshTimeline = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(javafx.util.Duration.seconds(10),
                                new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                                    public void handle(javafx.event.ActionEvent event) {
                                        refreshStats();
                                    }
                                }));
                refreshTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
                refreshTimeline.play();
            }
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR in DashboardController.initialize(): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DashboardController: initialize() FINISHED");
    }

    @FXML
    private Label currentUserLabel;

    private void configureMenuBasedOnRole() {
        com.cotizia.cotizia.models.Utilisateur user = AuthenticationService.getCurrentUser();
        String role = user.getRole();

        if (currentUserLabel != null) {
            currentUserLabel.setText(user.getNom() + " " + user.getPrenom() + "\n(" + role + ")");
        } else {
            System.err.println("DEBUG: currentUserLabel IS NULL! FXML Injection failed.");
        }

        if ("ADHERANT".equalsIgnoreCase(role)) {
            usersBtn.setVisible(false);
            usersBtn.setManaged(false); // Remove space
            mouchardBtn.setVisible(false);
            mouchardBtn.setManaged(false);
            cyclesBtn.setText("Mes Cycles");
        } else if ("COLLECTEUR".equalsIgnoreCase(role)) {
            usersBtn.setVisible(false);
            usersBtn.setManaged(false);
            mouchardBtn.setVisible(false);
            mouchardBtn.setManaged(false);
        }
        // Admin sees all
    }

    @FXML
    private void handleLogout() throws IOException {
        System.out.println("Logging out...");
        authService.logout();
        Cotizia.setRoot("login");
    }

    @FXML
    private void showCycles() {
        System.out.println("Navigating to Cycles...");
        if ("ADHERANT".equalsIgnoreCase(AuthenticationService.getCurrentUser().getRole())) {
            loadView("my_cycles");
        } else {
            loadView("cycle_list");
        }
    }

    @FXML
    private void showUtilisateurs() {
        System.out.println("Navigating to Utilisateurs...");
        loadView("user_list");
    }

    @FXML
    private void showMouchard() {
        System.out.println("Navigating to Mouchard...");
        loadView("mouchard_view");
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cotizia/cotizia/ui/" + fxml + ".fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Error loading view " + fxml + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshStats() {
        System.out.println("Refreshing stats...");
        if (totalCyclesLabel != null) {
            try {
                int cycles = cycleDAO.countAll();
                System.out.println("Cycles count: " + cycles);
                totalCyclesLabel.setText(String.valueOf(cycles));

                int users = utilisateurDAO.countAllUsers();
                System.out.println("Users count: " + users);
                totalUsersLabel.setText(String.valueOf(users));

                double paid = echeanceDAO.sumTotalPaid();
                System.out.println("Total paid: " + paid);
                totalCollectedLabel.setText(String.format("%.2f FG", paid));

                // Load recent activities
                if (recentActivitiesBox != null) {
                    com.cotizia.cotizia.implementation.MouchardDAO mouchardDAO = new com.cotizia.cotizia.implementation.MouchardDAO();
                    java.util.List logs = mouchardDAO.getRecentLogs(5);

                    recentActivitiesBox.getChildren().clear();
                    Label title = new Label("Activités Récentes");
                    title.getStyleClass().add("subtitle-label");
                    title.setStyle("-fx-padding: 0 0 10 0;");
                    recentActivitiesBox.getChildren().add(title);

                    if (logs == null || logs.isEmpty()) {
                        Label placeholder = new Label("Aucune activité récente.");
                        placeholder.setStyle("-fx-text-fill: #90a4ae;");
                        recentActivitiesBox.getChildren().add(placeholder);
                    } else {
                        for (Object logObj : logs) {
                            String log = (String) logObj;
                            Label logLabel = new Label(log);
                            logLabel.setStyle("-fx-text-fill: #455a64; -fx-padding: 5 0 5 0; -fx-font-size: 13px;");
                            recentActivitiesBox.getChildren().add(logLabel);
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Error refreshing stats: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println(
                    "WARNING: totalCyclesLabel is null! We might be in a sub-view. Not reloading dashboard to avoid loop.");
        }
    }
}
