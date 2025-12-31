/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.controllers;

import com.iwish.models.User;
import com.iwish.network.NetworkService;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author ITEi
 */
public class SigninController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField initialBalanceField;
    
    @FXML
    private void handleRegister(ActionEvent event) throws IOException, ClassNotFoundException{
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        String balanceStr = initialBalanceField.getText();
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || balanceStr.isEmpty()){
            showAlert(Alert.AlertType.ERROR, "Form Error!","Please fill all fields.");
            return;
        }
        
        if(!password.equals(confirm)){
            showAlert(Alert.AlertType.ERROR, "Password Error!", "Passwords do not match.");
            return;
        }
        
        try{
            double balance = Double.parseDouble(balanceStr);
            
            //Create user Model
            User newUser = new User(username, password, email, balance);
            
            //Get network connection
            NetworkService network = NetworkService.getInstance();
            
            //send the User object to the server
            network.sendRequest(newUser);
            
            //wait for server response
            String response = (String) network.receiveResponse();
            
            if("REGISTRATION_SUCCESS".equals(response)){
                showAlert(Alert.AlertType.INFORMATION, "Success", "Registeration successful!");
                backToLogin(event);
            }else{
                showAlert(Alert.AlertType.ERROR, "Failed", "Registeration failed. Username might be taken.");
            }
        }catch(Exception e){
            showAlert(Alert.AlertType.ERROR, "Error!", "Could not connect to server.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void backToLogin(ActionEvent event) throws IOException{
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/com/iwish/ui/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("i-Wish - Login");
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //helper method for pop-up alerts
    private void showAlert(Alert.AlertType alertType, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
