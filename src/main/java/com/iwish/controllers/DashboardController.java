/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.controllers;

import com.iwish.models.User;
import com.iwish.utils.UserSession;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 *
 * @author ITEi
 */
public class DashboardController implements Initializable{
    @FXML private Label welcomeLabel;
    @FXML private Label balanceLabel;
    @FXML private Tab friendsTab;
    @FXML private TabPane mainTabPane;
    
    @FXML
    public void initialize(URL url, ResourceBundle rb){
        User currentUser = UserSession.getInstance().getUser();
        if(currentUser != null){
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
            balanceLabel.setText("Balance: $" + currentUser.getBalance());
        }
        
        //Add listener to detect when tab changes
        friendsTab.selectedProperty().addListener((observable, oldValue, newValue)->{
            if(newValue){//If the tab is selected
                loadSocialContent();
            }
        });
    }
    
    private void loadSocialContent(){
        try{
            //Load the social.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/iwish/ui/social.fxml"));
            Parent socialNode = loader.load();
            
            //Set the content of the tab to the social view
            friendsTab.setContent(socialNode);
        }catch(IOException e){
            e.printStackTrace();
        }
    }




}
