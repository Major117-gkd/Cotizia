package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.services.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private javafx.scene.control.CheckBox showPasswordCheckBox;
    @FXML
    private Label errorLabel;

    private AuthenticationService authService = new AuthenticationService();

    public LoginController() {
        System.out.println("LoginController: Constructor called");
    }

    @FXML
    public void initialize() {
        System.out.println("LoginController: initialize() called");
        if (passwordTextField != null && passwordField != null) {
            // Bind text properties bidirectionally
            passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        }
    }

    @FXML
    public void togglePasswordVisibility() {
        System.out.println("togglePasswordVisibility called");
        if (showPasswordCheckBox.isSelected()) {
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
        }
    }

    @FXML
    public void handleLogin() {
        System.out.println("Login Button Clicked");
        String email = emailField.getText();
        String password = passwordField.getText();
        System.out.println("Attempting login for: " + email);

        try {
            if (authService.login(email, password)) {
                System.out.println("Login success!");
                Cotizia.setRoot("dashboard");
            } else {
                System.out.println("Login failed: Invalid credentials");
                showError("Echec de connexion", "Email ou mot de passe incorrect.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            java.io.PrintWriter pw = null;
            try {
                pw = new java.io.PrintWriter("login_error.log");
                e.printStackTrace(pw);
            } catch (java.io.FileNotFoundException ex) {
                ex.printStackTrace();
            } finally {
                if (pw != null) {
                    pw.close();
                }
            }
            System.err.println("Login Exception: " + e.getMessage());
            showError("Erreur Système", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
