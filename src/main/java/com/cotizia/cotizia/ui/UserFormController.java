package com.cotizia.cotizia.ui;

import com.cotizia.cotizia.Cotizia;
import com.cotizia.cotizia.implementation.UtilisateurDAO;
import com.cotizia.cotizia.models.Adherant;
import com.cotizia.cotizia.models.Collecteur;
import com.cotizia.cotizia.models.Utilisateur;
import com.cotizia.cotizia.utils.SelectionContext;
import com.cotizia.cotizia.implementation.MouchardDAO;
import com.cotizia.cotizia.services.AuthenticationService;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserFormController {

    @FXML
    private Label titleLabel;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private javafx.scene.control.CheckBox showPasswordCheckBox;
    @FXML
    private ComboBox<String> roleBox;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField telephoneField;
    @FXML
    private Label errorLabel;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private MouchardDAO mouchardDAO = new MouchardDAO();
    private Utilisateur editUser;

    @FXML
    public void initialize() {
        if (passwordTextField != null && passwordField != null) {
            passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        }

        editUser = SelectionContext.getInstance().getSelectedUser();
        if (editUser != null) {
            titleLabel.setText("Modifier Utilisateur");
            nomField.setText(editUser.getNom());
            prenomField.setText(editUser.getPrenom());
            emailField.setText(editUser.getEmail());
            passwordField.setText(editUser.getMotDePasse());
            roleBox.setValue(editUser.getRole());
            adresseField.setText(editUser.getAdresse());
            telephoneField.setText(editUser.getTelephone());

            // Disable role change for existing user to avoid class cast issues if we don't
            // re-instantiate
            // In a real app we might handle role change more carefully.
            // roleBox.setDisable(true);
        } else {
            titleLabel.setText("Nouvel Utilisateur");
        }
    }

    @FXML
    private void togglePasswordVisibility() {
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
    private void handleSave() {
        try {
            if (editUser == null) {
                // Validate inputs
                if (!com.cotizia.cotizia.utils.ValidatorUtils.isValidEmail(emailField.getText())) {
                    errorLabel.setText("Email invalide.");
                    return;
                }
                if (!com.cotizia.cotizia.utils.ValidatorUtils.isValidPhone(telephoneField.getText())) {
                    errorLabel.setText("Téléphone invalide.");
                    return;
                }

                // Create new
                String role = roleBox.getValue();
                Utilisateur newUser;
                if ("COLLECTEUR".equals(role)) {
                    newUser = new Collecteur();
                    ((Collecteur) newUser).setMatricule("MAT-" + System.currentTimeMillis());
                } else if ("ADHERANT".equals(role)) {
                    newUser = new Adherant();
                } else {
                    newUser = new Utilisateur();
                }

                populateUser(newUser);
                newUser.setRole(role); // ensure role is set in base class too
                utilisateurDAO.create(newUser);
                mouchardDAO.log("Création utilisateur: " + newUser.getEmail(),
                        AuthenticationService.getCurrentUser().getId());

            } else {
                // Update existing
                // Validate inputs
                if (!com.cotizia.cotizia.utils.ValidatorUtils.isValidEmail(emailField.getText())) {
                    errorLabel.setText("Email invalide.");
                    return;
                }
                if (!com.cotizia.cotizia.utils.ValidatorUtils.isValidPhone(telephoneField.getText())) {
                    errorLabel.setText("Téléphone invalide.");
                    return;
                }

                // Warning: Changing role might require re-creating object in DB or handling
                // type.
                // For simplicity here, we assume role doesn't change OR we basic update
                populateUser(editUser);
                utilisateurDAO.update(editUser);
                mouchardDAO.log("Modification utilisateur: " + editUser.getEmail(),
                        AuthenticationService.getCurrentUser().getId());
            }

            Cotizia.setRoot("user_list");

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la sauvegarde.");
        }
    }

    private void populateUser(Utilisateur u) {
        u.setNom(nomField.getText());
        u.setPrenom(prenomField.getText());
        u.setEmail(emailField.getText());
        u.setMotDePasse(passwordField.getText());
        u.setRole(roleBox.getValue());
        u.setAdresse(adresseField.getText());
        u.setTelephone(telephoneField.getText());
    }

    @FXML
    private void handleCancel() throws IOException {
        Cotizia.setRoot("user_list");
    }
}
